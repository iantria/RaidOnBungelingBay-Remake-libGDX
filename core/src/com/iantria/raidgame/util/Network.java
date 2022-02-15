package com.iantria.raidgame.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;

public class Network implements Net.HttpResponseListener
{
    public String result = "-1";
    public int errorCode = -1;
    public int statusCode = -1;

    Net.HttpRequest request;

    public Network(String URL, String q) {
        //System.out.println(URL + "?" + q);
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        request = requestBuilder.newRequest().method(Net.HttpMethods.GET).url(URL).content(q).build();
        Gdx.net.sendHttpRequest(request, this);
    }

    @Override
    public void handleHttpResponse(Net.HttpResponse httpResponse) {
        if( httpResponse.getStatus().getStatusCode() != 200) {
            //ERROR
            errorCode = httpResponse.getStatus().getStatusCode();
            //System.out.println("HTTP ERROR");
        } else {
            result = httpResponse.getResultAsString();
            statusCode = httpResponse.getStatus().getStatusCode();
            //System.out.println("Result:" + result + "  statusCode:" + statusCode);
            //byte[] byteResult = httpResponse.getResult(); //you can also get result as String by using httpResponse.getResultAsString();
        }
    }

    @Override
    public void failed(Throwable t) {
        // TODO Auto-generated method stub
    }

    @Override
    public void cancelled() {
        // TODO Auto-generated method stub
    }
}