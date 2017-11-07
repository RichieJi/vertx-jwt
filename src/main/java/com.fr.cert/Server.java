package com.fr.cert;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;

import java.util.Date;

@SuppressWarnings("all")
public class Server extends AbstractVerticle {

    // richie:随机生成的一个key，用于做测试
    private static byte[] key = MacProvider.generateKey().getEncoded();

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);

        router.route("/").handler(context -> {
            context.response().putHeader("content-type", "text/html").end("Hello World!");
        });
        router.route("/login").handler(context -> {
            HttpServerRequest request = context.request();
            String username = request.getParam("username");
            String password = request.getParam("password");
            if (isAuthSuccess(username, password)) {
                long nowMillis = System.currentTimeMillis();
                Date now = new Date(nowMillis);
                JwtBuilder builder = Jwts.builder()
                        .setId(username)
                        .setIssuedAt(now)
                        .setAudience(username);
                String compact = builder.signWith(SignatureAlgorithm.HS256, key).compact();
                context.response().end(compact);
            } else {
                context.response().end("Authentication failed, error username or password!");
            }
        });
        router.route("/get").handler(context -> {
            HttpServerRequest request = context.request();
            String username = request.getParam("token");
            try {
                Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(getToken(request)).getBody();
                String audience = claims.getAudience();
                context.response().end(audience);
            } catch (Exception e) {
                context.response().end("Invalid token!");
            }
        });
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

    private boolean isAuthSuccess(String username, String password) {
        return username.equals(password);
    }

    /**
     * 从http请求中获取token
     * @param req http请求
     * @return token内容
     */
    private String getToken(HttpServerRequest req) {
        String authorization = req.getHeader("Authorization");
        if (authorization == null) {
            authorization = req.getParam("token");
        }
        if (authorization == null) {
            return "";
        }
        // OAuth2认证的格式规范，必须以Bearer 开头
        if (authorization.contains("Bearer ")) {
            authorization = authorization.substring(7);
        }
        return authorization;
    }
}