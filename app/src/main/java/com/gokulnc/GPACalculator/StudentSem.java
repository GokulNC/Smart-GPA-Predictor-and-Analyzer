package com.gokulnc.GPACalculator;


/**
 * Created by Gokul on 30-Aug-16.
 */
public class StudentSem {
    char[] grades;
    short points[];
    float gpa;
    Sem sem;

    StudentSem(Sem s, char[] g) {
        sem = s;
        grades = g;
    }

    void gradesToPoints() throws Exception {
        points = new short[sem.getSubjects()];
        for(int i=0; i<sem.getSubjects();++i) {
            switch(grades[i]) {
                case 'S': points[i] = 10; break;
                case 'A': points[i] = 9; break;
                case 'B': points[i] = 8; break;
                case 'C': points[i] = 7; break;
                case 'D': points[i] = 6; break;
                case 'E': points[i] = 5; break;
                case 'U': points[i] = 0; break;
                default: throw new Exception("Unknown Grade");
            }
        }
    }

    public float calculateGPA() throws Exception {
        gradesToPoints();
        gpa = 0.0f;
        for(int i=0; i<sem.getSubjects(); ++i) {
            gpa += sem.credits[i]*points[i];
        }
        gpa = (float) (gpa/sem.getTotalCredits());
        return gpa;
    }

}