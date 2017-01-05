package com.gokulnc.GPACalculator;

/**
 * Created by Gokul on 30-Aug-16.
 */
public class Sem {
    private short subjects;
    short[] credits;
    private short totalCredits;
    String[] subjectNames;
    String[] subjectCodes;

    Sem(short n, short[] c, String[] names, String[] codes) {
        subjects = n;
        credits = c;
        subjectNames = names;
        subjectCodes = codes;
        calculateTotalCredits();
    }

    public short[] getCredits() {
        return credits;
    }

    public String[] getSubjectNames() {
        return subjectNames;
    }

    public String[] getSubjectCode() {
        return subjectCodes;
    }
    public short getSubjects() {
        return subjects;
    }

    public short getTotalCredits() {
        return totalCredits;
    }

    short calculateTotalCredits() {
        totalCredits = 0;
        for(short i: credits) {
            totalCredits += + i;
        }
        return getTotalCredits();
    }

}
