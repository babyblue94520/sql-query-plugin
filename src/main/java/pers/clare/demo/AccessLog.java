package pers.clare.demo;

import pers.clare.sqlquery.annotation.SQLEntityTest;

import javax.persistence.*;
import java.io.Serializable;
//import pers.clare.sqlquery.processor.SQLEntityProcessor;

/**
 * The persistent class for the AccessLog database table.
 */
@SQLEntityTest
public class AccessLog implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Id
    private Long time;

    private String ip;

    private String location;

    private String remark;

    @Lob
    @Column(name = "request_header",updatable = false)
    private String requestHeader;

    @Lob
    @Column(name = "request_parameter",insertable = false)
    private String requestParameter;
    @Lob
    @Column(name = "request_body")
    private String requestBody;

    @Lob
    @Column(name = "response_header")
    private String responseHeader;

    @Lob
    @Column(name = "response_content")
    private String responseContent;

    private String service;

    @Column(name = "session_id")
    private String sessionId;

    private Integer status;

    private String url;

    private String method;

    private String user;

    private Long ms;

    public static void main(String[] args) {
        AccessLog accessLog = new AccessLog();
    }
}
