package com.example.myapplication.util;

import com.example.myapplication.datasystem.DataProcessor;

/**
 * Standard visitor interface
 */
public interface Visitor
{

    void visitSampler(DataProcessor dp);

}
