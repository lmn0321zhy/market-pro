package com.lmn.common.tag;

import javax.annotation.PostConstruct;

public interface Plugin {

    @PostConstruct
    public void init() throws Exception;
}
