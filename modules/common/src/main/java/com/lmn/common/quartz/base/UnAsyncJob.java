package com.lmn.common.quartz.base;


import com.lmn.common.quartz.base.AsyncJob;
import org.quartz.*;



/**
 * Job有状态实现类，不允许并发执行
 * 若一个方法一次执行不完下次轮转时则等待该方法执行完后才执行下一次操作
 * 主要是通过注解：@DisallowConcurrentExecution
 */
@DisallowConcurrentExecution
public class UnAsyncJob extends AsyncJob {
}
