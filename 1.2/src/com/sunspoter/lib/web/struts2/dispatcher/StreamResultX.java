package com.sunspoter.lib.web.struts2.dispatcher;

import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.dispatcher.StreamResult;
import com.opensymphony.xwork2.ActionInvocation;

/**
 * 该类扩展了struts2中的result-type为stream的实体类，并重写了doExecute方法。
 * 该类修正了源于ResultStream的一些错误处理，在文档下载取消时释放HttpResponse对象的引用。
 * @author Jimmy Song
 * @version 1.0 
 * @see https://github.com/41zone/StreamResultX
 */
public class StreamResultX extends StreamResult {

	private static final long serialVersionUID = -8275283556955657976L;

	/**
	 * 扩展重写StreamResult方法
	 */
	protected void doExecute(String finalLocation, ActionInvocation invocation)
			throws Exception {
		resolveParamsFromStack(invocation.getStack(), invocation);

		OutputStream oOutput = null;

		try {
			if (inputStream == null) {
				// Find the inputstream from the invocation variable stack
				inputStream = (InputStream) invocation.getStack().findValue(
						conditionalParse(inputName, invocation));
			}

			if (inputStream == null) {
				String msg = ("StreamResultX : Can not find a java.io.InputStream with the name ["
						+ inputName + "] in the invocation stack. " + "Check the <param name=\"inputName\"> tag specified for this action.");
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}

			// Find the Response in context
			HttpServletResponse oResponse = (HttpServletResponse) invocation
					.getInvocationContext().get(HTTP_RESPONSE);

			// Set the content type
			if (contentCharSet != null && !contentCharSet.equals("")) {
				oResponse.setContentType(conditionalParse(contentType,
						invocation)
						+ ";charset=" + contentCharSet);
			} else {
				oResponse.setContentType(conditionalParse(contentType,
						invocation));
			}

			// Set the content length
			if (contentLength != null) {
				String _contentLength = conditionalParse(contentLength,
						invocation);
				int _contentLengthAsInt = -1;
				try {
					_contentLengthAsInt = Integer.parseInt(_contentLength);
					if (_contentLengthAsInt >= 0) {
						oResponse.setContentLength(_contentLengthAsInt);
					}
				} catch (NumberFormatException e) {
					LOG
							.warn(
									"StreamResultX warn : failed to recongnize "
											+ _contentLength
											+ " as a number, contentLength header will not be set",
									e);
				}
			}
 
			// Set the content-disposition
			if (contentDisposition != null) {
				oResponse.addHeader("Content-Disposition", conditionalParse(
						contentDisposition, invocation));
			}

			// Set the cache control headers if neccessary
			if (!allowCaching) {
				oResponse.addHeader("Pragma", "no-cache");
				oResponse.addHeader("Cache-Control", "no-cache");
			}
			// Get the outputstream
			oOutput = oResponse.getOutputStream();

			if (LOG.isDebugEnabled()) {
				LOG.debug("StreamResultX : Streaming result [" + inputName
						+ "] type=[" + contentType + "] length=["
						+ contentLength + "] content-disposition=["
						+ contentDisposition + "] charset=[" + contentCharSet
						+ "]");
			}

			// Copy input to output

			byte[] oBuff = new byte[bufferSize];
			int iSize;
			try {
				LOG
						.debug("StreamResultX : Streaming to output buffer +++ START +++");
				while (-1 != (iSize = inputStream.read(oBuff))) {
					oOutput.write(oBuff, 0, iSize);
				}
				LOG
						.debug("StreamResultX : Streaming to output buffer +++ END +++");
				// Flush
				oOutput.flush();
			} catch (Exception e) {
				LOG.warn("StreamResultX Warn : socket write error");
				if (oOutput != null) {
					try {
						oOutput.close();
					} catch (Exception e1) {
						oOutput = null;
					}
				}
			} finally {
				if (inputStream != null)
					inputStream.close();
				if (oOutput != null)
					oOutput.close();
			}
		} finally {
			if (inputStream != null)
				inputStream.close();
			if (oOutput != null)
				oOutput.close();
		}
	}
}
