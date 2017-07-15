package feeker.net.tools.util;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class HttpClientUtils {
    /**
     * @param client HttpClient
     * @param url    requestUrl
     * @return responseBody
     */
    public static String httpGet(HttpClient client, String url) {
        String responseBody = null;
        try {
            GetMethod getMethod = new GetMethod(url);
            getMethod.setRequestHeader("Content-Type", "text/plain; charset=ISO-8859-1");
            getMethod.setRequestHeader("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
            int status = client.executeMethod(getMethod);
            if (status == HttpStatus.SC_OK) {
//                responseBody = getMethod.getResponseBodyAsString();
                InputStream responseStream = getMethod.getResponseBodyAsStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int i;
                while ((i = responseStream.read()) != -1) {
                    byteArrayOutputStream.write(i);
                }
                responseBody = byteArrayOutputStream.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseBody;
    }

    /**
     * @param client HttpClient
     * @param url    requestUrl
     * @param params NameValuePair[]
     * @return string responseBody
     */
    @SuppressWarnings("unused")
    public static String httpGet(HttpClient client, String url, NameValuePair[] params) {
        String responseBody = null;
        if (StringUtils.isBlank(url)) {
            return "";
        }
        try {
            GetMethod getMethod = new GetMethod(url);
            getMethod.setRequestHeader("Content-Type", "text/plain; charset=ISO-8859-1");
            getMethod.setRequestHeader("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
            getMethod.setQueryString(params);
            int status = client.executeMethod(getMethod);
            if (status == HttpStatus.SC_OK)
                responseBody = getMethod.getResponseBodyAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseBody;
    }

    /**
     * @param client        HttpClient
     * @param url           requestUrl
     * @param requestEntity RequestEntity
     * @return string responseBody
     */
    @SuppressWarnings("unused")
    public static String httpPost(HttpClient client, String url, RequestEntity requestEntity) {
        String responseBody = null;
        try {
            PostMethod httpPost = new PostMethod(url);
            httpPost.setRequestHeader("Content-Type", "text/plain; charset=ISO-8859-1");
            httpPost.setRequestHeader("Accept", "text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2");
            httpPost.setRequestEntity(requestEntity);
            int status = client.executeMethod(httpPost);
            if (status == HttpStatus.SC_OK)
                responseBody = httpPost.getResponseBodyAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseBody;
    }

    /**
     * @param client HttpClient
     * @param url    requestUrl
     * @param params NameValuePair[]
     * @return responseBody
     */
    public static String httpPost(HttpClient client, String url, NameValuePair[] params) {
        String responseBody = null;
        try {
            PostMethod httpPost = new PostMethod(url);
            httpPost.getParams().setContentCharset("UTF-8");

            httpPost.setRequestBody(params);
            int status = client.executeMethod(httpPost);
            if (status == HttpStatus.SC_OK)
                responseBody = httpPost.getResponseBodyAsString();

            if (status == HttpStatus.SC_MOVED_PERMANENTLY
                    || status == HttpStatus.SC_MOVED_TEMPORARILY) {
                // 从头中取出转向的地址
                Header locationHeader = httpPost.getResponseHeader("location");
                String location;
                if (locationHeader != null) {
                    location = locationHeader.getValue();
                    System.out.println("转向的地址:" + location);
                } else {
                    System.err.println("Location field value is null.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseBody;
    }

    /**
     * @param charset       encoding
     * @param url           requestUrl
     * @param requestEntity RequestEntity
     * @return responseBody String
     */
    public static String httpPost(String charset, String url, RequestEntity requestEntity) {
        String responseBody = null;
        HttpClient httpclient = new HttpClient();
        PostMethod post = new PostMethod(url);
        try {
            post.setRequestEntity(requestEntity);
            post.getParams().setContentCharset(charset);
            httpclient.executeMethod(post);
            responseBody = post.getResponseBodyAsString();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            post.releaseConnection();
        }
        return responseBody;
    }

    /**
     * @param httpClient HttpClient
     * @param url        requestUrl
     * @param data       String
     * @return String
     */
    @SuppressWarnings("unused")
    public static String httpPostBodyData(HttpClient httpClient, String url, String data) {
        try {
            return httpPost("UTF-8", url, new StringRequestEntity(data, "application/json", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
