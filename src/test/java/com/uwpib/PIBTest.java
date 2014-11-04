package com.uwpib;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PIBTest
{
    @Test
    public void runTest()
    {
        PIB pib = new PIB();

        assertEquals("Testing getTestString()", "test", pib.getTestString());
    }
}
