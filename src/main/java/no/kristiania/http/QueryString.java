package no.kristiania.http;

import java.util.HashMap;
import java.util.Map;

public class QueryString {
    private Map<String, String> queryPairs = new HashMap<>();

    public QueryString(String queryString) {

        String[] queryParameters = queryString.split("&");

        for (String parameter : queryParameters) {
            String[] parameterPair = parameter.split("=");
            queryPairs.put(parameterPair[0], parameterPair[1]);
        }
    }

    public static Map<String, String> queryStringToHashMap(String queryString) {
        Map<String, String> queryMap = new HashMap<>();

        String[] queryParameters = queryString.split("&");
        for (String parameter : queryParameters) {
            String[] parameterPair = parameter.split("=");
            if(parameterPair.length > 1) {
                queryMap.put(parameterPair[0], parameterPair[1]);
            }
        }

        return queryMap;
    }

    public static void putQueryParametersIntoHttpMessageHeaders(HttpMessage httpMessage, String queryString) {
        String[] queryParameters = queryString.split("&");

        for (String parameter : queryParameters) {
            String[] parameterPair = parameter.split("=");
            if(parameterPair.length > 1) {
                httpMessage.setHeader(parameterPair[0], parameterPair[1]);
            }
        }
    }
}
