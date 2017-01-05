package com.gokulnc.GPACalculator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Gokul on 30-Aug-16.
 */
public class CompleteCourse {

    float cgpa;
    float[] gpa;
    public Sem[] s;
    StudentSem[] semester;
    short numberOfSems;
    boolean isGradesGiven = false;
    InputStream input;

    public CompleteCourse(InputStream i) {
        input = i;
    }

    public void fetchSemData() throws IOException {

        BufferedReader br = null;
        String currentLine;
        int n = 0;

         br = new BufferedReader(new InputStreamReader(input));

        if((currentLine = br.readLine()) != null) //reading numberOfSems
            n = Integer.parseInt(currentLine);
        if(n==0) throw new IOException("Unable to read numberOfSems");

        numberOfSems = (short) n;
        s = new Sem[numberOfSems];
        semester = new StudentSem[numberOfSems];

        int i = 0;
        while(true) {
            currentLine = br.readLine();
            if(currentLine!= null && currentLine.contains("sem")) { //to write
                currentLine = currentLine.replace("sem ","");
                int semNo = Integer.parseInt(currentLine.replaceAll("[^0-9]", "")); ++i;
                int numSubjects;
                if(i != semNo) throw new IOException("Error reading. semNo mismatch");
                if((currentLine = br.readLine()) == null) throw new IOException("Error reading");
                else numSubjects = Integer.parseInt(currentLine);
                String codes[] = new String[numSubjects];
                String names[] = new String[numSubjects];
                short credits[] =  new short[numSubjects];
                int j = 0;
                while((currentLine = br.readLine()) != null && !currentLine.contains("EOS")) {
                    String s[] = currentLine.split(" ");
                    if(s.length != 3)  throw new IOException("Error parsing subject "+(j+1)+" in sem "+i);
                    codes[j] = s[0];
                    names[j] = s[1];
                    credits[j] = (short) Integer.parseInt(s[2].replaceAll("[^0-9]", ""));
                    ++j;
                }
                if(j!=numSubjects) throw new IOException("Number of subjects mismatch in sem "+i);
                if(currentLine.contains("EOS")) {
                    s[i-1] = new Sem( (short) numSubjects, credits, names, codes);

                } else throw new IOException("EOS not found");

            } else if(currentLine.contains("EOF")) { //write it
                break;
            } else throw  new IOException("Error reading file");
        }

        try {
            if (br != null)br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveGPAsWithoutGrades(float gpa[]) {
        int i;
        this.gpa = new float[numberOfSems];
        for(i=0; i<gpa.length; ++i) this.gpa[i] = gpa[i];
        for(; i<numberOfSems; ++i) this.gpa[i] = 0.0f;
    }

    int validateGPAs() {
        if(gpa == null) return -1;
        for(int i=0; i<gpa.length; ++i) {
            if(gpa[i]>0 && gpa[i]<=10) continue;
            else return i+1;
        }
        return gpa.length;
    }

    public float calculateCGPA() throws Exception {
        int semsFound = validateGPAs();
        if(semsFound < 1) throw new Exception("Enter all GPAs properly");
        cgpa = 0.0f;
        int credits = 0;
        for(int i=0; i<gpa.length; ++i) {
            cgpa += gpa[i]*s[i].getTotalCredits();
            credits += s[i].getTotalCredits();
        }
        cgpa = cgpa/credits;
        return cgpa;
    }

    short calculateAllSemCredits() {
        short creditsTotal = 0;
        for(int i=0; i<numberOfSems; ++i) {
            creditsTotal += s[i].getTotalCredits();
        }
        return creditsTotal;
    }

    short[] getAllSemCredits() {
        short credits[] = new short[numberOfSems];
        for(int i=0; i<numberOfSems; i++) credits[i] = s[i].getTotalCredits();
        return credits;
    }

    public float calculatePrediction(float target_cgpa) throws Exception{
        int semsFound = validateGPAs();
        if(semsFound < 1) throw new Exception("Enter all GPAs properly");

        short credits[] = getAllSemCredits();

        float predictedGPA = 0;
        int i = 0;
        float temp = 0.0f;
        for(i=0; i<numberOfSems; ++i) {
            if(gpa[i] == 0.0) break;
            temp += (double) credits[i]*gpa[i];
        }

        short tempCredits = 0;
        for(; i<numberOfSems; ++i) tempCredits += credits[i];

        float RHS = target_cgpa*calculateAllSemCredits();
        RHS -= temp;
        predictedGPA = RHS/tempCredits;

        return predictedGPA;
    }

}