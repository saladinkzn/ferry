package ru.shadam.ferry.core;

import ru.shadam.ferry.factory.executor.MethodExecutor;
import ru.shadam.ferry.factory.response.DefaultResponseWrapper;
import ru.shadam.ferry.factory.response.ResponseWrapper;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

/**
 * @author timur.shakurov@dz.ru
 */
public class CoreMethodExecutor implements MethodExecutor {
    private final String url;
    private final String method;

    public CoreMethodExecutor(String url, String method) {
        this.url = url;
        this.method = method;
    }

    @Override
    public ResponseWrapper execute(Map<String, ?> parameters, Map<String, ?> pathVariables, String requestBody) throws IOException {
        String urlBuilder = url;
        for(Map.Entry<String, ?> entry : pathVariables.entrySet()) {
            final String pathVariableName = entry.getKey();
            final String pathVariableValue = String.valueOf(entry.getValue());
            //
            urlBuilder = url.replace(":" + pathVariableName, pathVariableValue);
        }
        StringBuilder paramBuilder = new StringBuilder(urlBuilder);
        final Iterator<? extends Map.Entry<String, ?>> iterator = parameters.entrySet().iterator();
        if(iterator.hasNext()) {
            paramBuilder.append("?");
            do {
                final Map.Entry<String, ?> first = iterator.next();
                paramBuilder
                        .append(URLEncoder.encode(first.getKey(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(String.valueOf(first.getValue()), "UTF-8"));
                if(iterator.hasNext()) {
                    paramBuilder.append("&");
                }
            } while (iterator.hasNext());
        }

        final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(paramBuilder.toString()).openConnection();
        httpURLConnection.setRequestMethod(method);
        if(requestBody != null) {
            final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
            try {
                outputStreamWriter.write(requestBody);
            } finally {
                outputStreamWriter.close();
            }
        }
        return new DefaultResponseWrapper(httpURLConnection.getInputStream());
    }
}
