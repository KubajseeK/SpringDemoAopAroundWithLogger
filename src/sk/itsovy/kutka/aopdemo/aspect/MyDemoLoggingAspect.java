package sk.itsovy.kutka.aopdemo.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import sk.itsovy.kutka.aopdemo.Account;

import java.util.List;
import java.util.logging.Logger;

@Aspect
@Component
@Order(2)

public class MyDemoLoggingAspect {

    private Logger logger = Logger.getLogger(getClass().getName());

    @Around("execution(* sk.itsovy.kutka.aopdemo.service.*.getFortune(..))")
    public Object aroundGetFortune(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        String method = proceedingJoinPoint.getSignature().toShortString();
        logger.info("Executing @Around on method" + method);

        long begin = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();

        long end = System.currentTimeMillis();
        long duration = end-begin;
        logger.info("Duration: " + duration / 1000.0 + " seconds");

        return result;
    }

    @After("execution(* sk.itsovy.kutka.aopdemo.dao.AccountDAO.findAccounts(..))")
    public void afterFinallyFindAccountsAdvice(JoinPoint joinPoint) {

        String method = joinPoint.getSignature().toShortString();
        logger.info("Executing @AfterFinally on method" + method);

    }

    @AfterThrowing(pointcut = "execution(* sk.itsovy.kutka.aopdemo.dao.AccountDAO.findAccounts(..))",throwing = "e")
    public void afterThrowingFindAccountsAdvice(JoinPoint joinPoint, Throwable e) {

        String method = joinPoint.getSignature().toShortString();
        logger.info("Executing @AfterThrowing on method" + method);

        logger.info("Exception: " + e);

    }

    @AfterReturning(
            pointcut = "execution(* sk.itsovy.kutka.aopdemo.dao.AccountDAO.findAccounts(..))",
            returning = "result")
    public void afterReturningFindAccountsAdvice(JoinPoint joinPoint, List<Account> result) {

        String method = joinPoint.getSignature().toShortString();
        logger.info("Executing AfterReturning on method: " + method);
        logger.info("Result is: " + result);

        convertAccountNamesToUpperCase(result);
    }

    private void convertAccountNamesToUpperCase(List<Account> result) {
        for (Account tempAccount : result) {
            String upperName = tempAccount.getName().toUpperCase();
            tempAccount.setName(upperName);
        }
    }

//    @Before("execution(public void add*())")

    @Before("sk.itsovy.kutka.aopdemo.aspect.LuvAopExpressions.forDaoPackageNoGetterSetter()")
    public void beforeAddAccountAdvice(JoinPoint joinPoint) {
        logger.info("ASPECT @BEFORE");

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        logger.info(methodSignature.toString());

        Object[] args = joinPoint.getArgs();
        for (Object tempArgs : args) {
            logger.info(tempArgs.toString());

            if (tempArgs instanceof Account) {
                Account account = (Account) tempArgs;
                logger.info("account name: " + account.getName());
            }
        }
    }


}
