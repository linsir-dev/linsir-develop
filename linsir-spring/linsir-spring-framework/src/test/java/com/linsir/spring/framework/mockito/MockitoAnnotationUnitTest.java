package com.linsir.spring.framework.mockito;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @ClassName : MockitoAnnotationUnitTest
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-06 19:46
 */

@ExtendWith(MockitoExtension.class)
public class MockitoAnnotationUnitTest {

    @Mock
    List<String> mockedList;


    List<Integer> intList;

    @BeforeEach
    public void init()
    {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void verifyTest(){
        // mock creation 创建mock对象
        List mockedList1 = mock(List.class);
        //using mock object 使用mock对象
        mockedList1.add("one");
        mockedList1.clear();
        //verification 验证
        verify(mockedList1).add("one");
        verify(mockedList1).clear();
    }

    @Test
    public void verifyTest2(){
        mockedList.add("one");
        verify(mockedList).add("one");
    }

    @Test
    public void testMockAnno()
    {
        Assertions.assertNotNull(mockedList);
        //Assertions.assertNotNull(intList);
    }
}
