package com.bae.myapplication.util;

import com.bae.myapplication.datasystem.DataProcessor;

/**
 * Standard visitor interface
 */
public interface Visitor
{
    void visitSampler(DataProcessor dp);
}
