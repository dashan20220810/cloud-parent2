package com.baisha.casinoweb.filter;//package com.qianyi.casinoweb.filter;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class XssFilter extends com.baisha.modulecommon.filter.XssFilter {
    @Override
    public void setPassList(List<String> passList) {
        passList.add("/error");
    }
}
