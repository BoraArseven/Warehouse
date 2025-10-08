package com.boracompany.airplanes;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AppTest {

    App application;

    @Before
    public void setUp() {
        application = new App();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void maintest() {
        assertEquals(3, application.initialmethod());
    }
}
