package com.appdsn.qa.common.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 
 * Created by baozhong 2016/02/01
 * 
 * RequestParams params = new RequestParams(); params.put("username", "james");
 * params.put("password", "123456"); params.put("email", "my&#064;email.com");
 * params.put("profile_picture", new File("pic.jpg")); // Upload a File
 * params.put("profile_picture2", someInputStream); // Upload an InputStream
 * params.put("profile_picture3", new ByteArrayInputStream(someBytes)); //
 * Upload some bytes
 */
public class RequestParams implements Serializable {

	private static final long serialVersionUID = 1L;

	public final static String APPLICATION_OCTET_STREAM = "application/octet-stream";

	public final static String APPLICATION_JSON = "application/json";

	protected final static String LOG_TAG = "RequestParams";
	protected final ConcurrentHashMap<String, String> urlParams = new ConcurrentHashMap<String, String>();
	protected final ConcurrentHashMap<String, BytesWrapper> bytesParams = new ConcurrentHashMap<String, BytesWrapper>();
	protected final ConcurrentHashMap<String, FileWrapper> fileParams = new ConcurrentHashMap<String, FileWrapper>();

	protected boolean isRepeatable;
	protected boolean forceMultipartEntity = false;
	protected boolean useJsonStreamer;
	protected String elapsedFieldInJsonStreamer = "_elapsed";
	protected boolean autoCloseInputStreams;
	protected String contentEncoding = "UTF-8";

	/**
	 * Constructs a new empty {@code RequestParams} instance.
	 */
	public RequestParams() {
		this((Map<String, String>) null);
	}

	/**
	 * Constructs a new RequestParams instance containing the key/value string
	 * params from the specified map.
	 * 
	 * @param source
	 *            the source key/value string map to add.
	 */
	public RequestParams(Map<String, String> source) {
		if (source != null) {
			for (Map.Entry<String, String> entry : source.entrySet()) {
				put(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * Constructs a new RequestParams instance and populate it with a single
	 * initial key/value string param.
	 * 
	 * @param key
	 *            the key name for the intial param.
	 * @param value
	 *            the value string for the initial param.
	 */
	@SuppressWarnings("serial")
	public RequestParams(final String key, final String value) {
		this(new HashMap<String, String>() {
			{
				put(key, value);
			}
		});
	}

	/**
	 * Sets content encoding for return value of {@link #()} and {@link #()}
	 * <p>
	 * &nbsp;
	 * </p>
	 * Default encoding is "UTF-8"
	 */
	public void setContentEncoding(final String encoding) {
		if (encoding != null) {
			this.contentEncoding = encoding;
		}
	}

	/**
	 * If set to true will force Content-Type header to `multipart/form-data`
	 * even if there are not Files or Streams to be send
	 * <p>
	 * &nbsp;
	 * </p>
	 * Default value is false
	 * 
	 * @param force
	 *            boolean, should declare content-type multipart/form-data even
	 *            without files or streams present
	 */
	public void setForceMultipartEntityContentType(boolean force) {
		this.forceMultipartEntity = force;
	}

	/**
	 * Adds a key/value string pair to the request.
	 * 
	 * @param key
	 *            the key name for the new param.
	 * @param value
	 *            the value string for the new param.
	 */
	public void put(String key, String value) {
		if (key != null && value != null) {
			urlParams.put(key, value);
		}
	}

	/**
	 * Adds a file to the request.
	 * 
	 * @param key
	 *            the key name for the new param.
	 * @param file
	 *            the file to add.
	 * @throws FileNotFoundException
	 *             throws if wrong File argument was passed
	 */
	public void put(String key, File file) throws FileNotFoundException {
		put(key, file, null, null);
	}

	/**
	 * Adds a file to the request with custom provided file name
	 * 
	 * @param key
	 *            the key name for the new param.
	 * @param file
	 *            the file to add.
	 * @param customFileName
	 *            file name to use instead of real file name
	 */
	public void put(String key, String customFileName, File file) {
		put(key, file, null, customFileName);
	}

	/**
	 * Adds a file to the request with custom provided file content-type
	 * 
	 * @param key
	 *            the key name for the new param.
	 * @param file
	 *            the file to add.
	 * @param contentType
	 *            the content type of the file, eg. application/json
	 */
	public void put(String key, File file, String contentType) {
		put(key, file, contentType, null);
	}

	/**
	 * Adds a file to the request with both custom provided file content-type
	 * and file name
	 * 
	 * @param key
	 *            the key name for the new param.
	 * @param file
	 *            the file to add.
	 * @param contentType
	 *            the content type of the file, eg. application/json
	 * @param customFileName
	 *            file name to use instead of real file name
	 */
	public void put(String key, File file, String contentType,
			String customFileName) {
		if (file == null || !file.exists()) {
			return;
		}
		if (key != null) {
			fileParams.put(key, new FileWrapper(file, contentType,
					customFileName));
		}
	}

	/**
	 * Adds an input stream to the request.
	 * 
	 * @param key
	 *            the key name for the new param.
	 */
	public void put(String key, byte[] bytes) {
		put(key, bytes, null);
	}

	/**
	 * Adds an input stream to the request.
	 * 
	 * @param key
	 *            the key name for the new param.
	 * @param name
	 *            the name of the stream.
	 */
	public void put(String key, byte[] bytes, String name) {
		put(key, bytes, name, null);
	}

	/**
	 * Adds an input stream to the request.
	 * 
	 * @param key
	 *            the key name for the new param.
	 * @param name
	 *            the name of the stream.
	 * @param contentType
	 *            the content type of the file, eg. application/json
	 */
	public void put(String key, byte[] bytes, String name, String contentType) {
		if (key != null && bytes != null) {
			bytesParams.put(key, new BytesWrapper(bytes, name, contentType));
		}
	}

	/**
	 * Adds a int value to the request.
	 * 
	 * @param key
	 *            the key name for the new param.
	 * @param value
	 *            the value int for the new param.
	 */
	public void put(String key, int value) {
		if (key != null) {
			urlParams.put(key, String.valueOf(value));
		}
	}

	/**
	 * Adds a long value to the request.
	 * 
	 * @param key
	 *            the key name for the new param.
	 * @param value
	 *            the value long for the new param.
	 */
	public void put(String key, long value) {
		if (key != null) {
			urlParams.put(key, String.valueOf(value));
		}
	}

	/**
	 * Removes a parameter from the request.
	 * 
	 * @param key
	 *            the key name for the parameter to remove.
	 */
	public void remove(String key) {
		urlParams.remove(key);
		bytesParams.remove(key);
		fileParams.remove(key);
	}

	/**
	 * Check if a parameter is defined.
	 * 
	 * @param key
	 *            the key name for the parameter to check existence.
	 * @return Boolean
	 */
	public boolean has(String key) {
		return urlParams.get(key) != null || bytesParams.get(key) != null
				|| fileParams.get(key) != null;
	}

/**
     * Returns an HttpEntity containing all request parameters.
     *
     * @return HttpEntity resulting HttpEntity to be included along with {@link
     * @throws IOException if one of the streams cannot be read
     */
	public RequestBody getRequestBody() throws IOException {
		if (!forceMultipartEntity && bytesParams.isEmpty()
				&& fileParams.isEmpty()) {
			return createFormBody();
		} else {
			return createMultipartBody();
		}
	}

	/* 如果是一串字符，可以这样 */
	public static RequestBody createStringBody(String content,
			String contentType) {
		if (contentType == null) {
			contentType = "application/json;charset=utf-8";
		}
		return RequestBody.create(MediaType.parse(contentType), content);
	}

	/* 如果只有一个文件，可以这样 */
	public static RequestBody createFileBody(File file, String contentType) {
		if (contentType == null) {
			contentType = "application/octet-stream;charset=utf-8";
		}
		return RequestBody.create(MediaType.parse(contentType), file);
	}

	private RequestBody createFormBody() {
		FormBody.Builder builder = new FormBody.Builder();
		for (String key : urlParams.keySet()) {
			builder.add(key, urlParams.get(key));
		}
		return builder.build();
	}

	private RequestBody createMultipartBody() throws IOException {
		MultipartBody.Builder builder = new MultipartBody.Builder()
				.setType(MultipartBody.FORM);
		// Add string params
		for (ConcurrentHashMap.Entry<String, String> entry : urlParams
				.entrySet()) {
			builder.addFormDataPart(entry.getKey(), entry.getValue());
		}
		// Add stream params
		for (ConcurrentHashMap.Entry<String, BytesWrapper> entry : bytesParams
				.entrySet()) {
			BytesWrapper bytesWrapper = entry.getValue();
			if (bytesWrapper.bytes != null) {
				RequestBody streamBody = RequestBody.create(
						MediaType.parse(bytesWrapper.contentType),
						bytesWrapper.bytes);
				builder.addFormDataPart(entry.getKey(), bytesWrapper.name,
						streamBody);
			}
		}
		// Add file params
		for (ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams
				.entrySet()) {
			FileWrapper fileWrapper = entry.getValue();
			RequestBody fileBody = RequestBody.create(
					MediaType.parse(fileWrapper.contentType), fileWrapper.file);
			builder.addFormDataPart(entry.getKey(), fileWrapper.customFileName,
					fileBody);
		}
		return builder.build();
	}

	public String getUrlWithParams(String url) {
		if (url == null) {
			return null;
		}
		final StringBuilder result = new StringBuilder();
		for (ConcurrentHashMap.Entry<String, String> entry : urlParams
				.entrySet()) {
			if (result.length() > 0) {
				result.append("&");
			}
			result.append(entry.getKey());
			if (entry.getValue() != null) {
				result.append("=");
				result.append(entry.getValue());
			}
		}

		if (!result.equals("") && !result.equals("?")) {
			url += url.contains("?") ? "&" : "?";
			url += result.toString();
		}
		return url;

	}

	public static class FileWrapper implements Serializable {
		private static final long serialVersionUID = 1L;
		public final File file;
		public final String contentType;
		public final String customFileName;

		public FileWrapper(File file, String contentType, String customFileName) {
			this.file = file;
			this.contentType = contentType == null ? APPLICATION_OCTET_STREAM
					: contentType;
			this.customFileName = customFileName;
		}
	}

	public static class BytesWrapper {
		public final byte[] bytes;
		public final String name;
		public final String contentType;

		public BytesWrapper(byte[] bytes, String name, String contentType) {
			this.bytes = bytes;
			this.name = name;
			this.contentType = contentType == null ? APPLICATION_OCTET_STREAM
					: contentType;

		}

	}
}
