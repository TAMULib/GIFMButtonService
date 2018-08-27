package edu.tamu.app;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("whitelist")
public class Whitelist {

    private static final Logger logger = LoggerFactory.getLogger(Whitelist.class);

    @Value("${app.whitelist}")
    private String[] whitelist;

    public boolean isAllowed(HttpServletRequest req) {
        String reqIp = req.getRemoteAddr();
        boolean allow = false;
        for (String ip : whitelist) {
            if (ip.trim().equals(reqIp.trim())) {
                logger.info("Allowing whitelist ip " + ip + " for " + req.getRequestURI());
                allow = true;
                break;
            }
        }
        return allow;
    }

}
