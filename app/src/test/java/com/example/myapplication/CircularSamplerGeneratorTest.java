package com.example.myapplication;

// Android API
import android.content.Context;

// Testing Libraries
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Testing cases for CircularSamplerGenerator and its subclasses.
 */
public class CircularSamplerGeneratorTest
{
    private static final String FAKE_STRING = "HELLO WORLD";

    @Mock
    private Context mockContext;

    @Before
    public void init()
    {
        mockContext = Mockito.mock(Context.class);
    }

    @Test
    public void mockContextTest()
    {
        assertNotNull(mockContext);
        when(mockContext.getString(0)).thenReturn(FAKE_STRING);
        assertEquals(FAKE_STRING, mockContext.getString(0));
    }

    @Test
    public void positionToColorTest()
    {

    }
}
