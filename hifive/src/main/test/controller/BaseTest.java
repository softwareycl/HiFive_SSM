package controller;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext.xml"})
@WebAppConfiguration("src/main/resources") 
public class BaseTest extends AbstractJUnit4SpringContextTests{
    @Before
    public void init() {
        //在运行测试之前的业务代码
    }
    
    @After
    public void after() {
        //在测试完成之后的业务代码
    }
}