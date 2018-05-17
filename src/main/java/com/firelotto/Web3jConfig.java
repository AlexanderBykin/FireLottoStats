package com.firelotto;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.ipc.UnixIpcService;
import org.web3j.protocol.ipc.WindowsIpcService;

import java.net.Proxy;
import java.util.concurrent.TimeUnit;

public class Web3jConfig {
    private static Logger log = LoggerFactory.getLogger(Web3jConfig.class);

    public Web3jService buildService(String clientAddress, Proxy proxy) {
        Web3jService web3jService;

        if (clientAddress == null || clientAddress.equals("")) {
            web3jService = new HttpService(createOkHttpClient(proxy));
        } else if (clientAddress.startsWith("http")) {
            web3jService = new HttpService(clientAddress, createOkHttpClient(proxy), false);
        } else if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            web3jService = new WindowsIpcService(clientAddress);
        } else {
            web3jService = new UnixIpcService(clientAddress);
        }

        return web3jService;
    }

    private OkHttpClient createOkHttpClient(Proxy proxy) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (proxy != null) builder.proxy(proxy);
        configureLogging(builder);
        configureTimeouts(builder);
        return builder.build();
    }

    private void configureTimeouts(OkHttpClient.Builder builder) {
        Long tos = 300L;
        if (tos != null) {
            builder.connectTimeout(tos, TimeUnit.SECONDS);
            builder.readTimeout(tos, TimeUnit.SECONDS);  // Sets the socket timeout too
            builder.writeTimeout(tos, TimeUnit.SECONDS);
        }
    }

    private void configureLogging(OkHttpClient.Builder builder) {
        if (log.isDebugEnabled()) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(log::debug);
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }
    }
}