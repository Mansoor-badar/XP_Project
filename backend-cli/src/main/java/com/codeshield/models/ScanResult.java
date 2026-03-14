package com.codeshield.models;

import java.util.List;
import com.codeshield.models.MethodResult;

public class ScanResult {

    private int totalComplexity;
    private double vulnerabilityDensity;
    private int redFlags;
    private int loc;
    private List<MethodResult> methods;

    public int getTotalComplexity() {
         return totalComplexity; 
        }
    public void setTotalComplexity(int totalComplexity) {
         this.totalComplexity = totalComplexity; 
        }

    public double getVulnerabilityDensity() { 
        return vulnerabilityDensity; 
    }
    public void setVulnerabilityDensity(double vulnerabilityDensity) { 
        this.vulnerabilityDensity = vulnerabilityDensity; 
    }

    public int getRedFlags() { 
        return redFlags; 
    }
    public void setRedFlags(int redFlags) { 
        this.redFlags = redFlags; 
    }

    public int getLoc() {
         return loc; 
        }
    public void setLoc(int loc) { 
        this.loc = loc;
     }

    public List<MethodResult> getMethods() {
         return methods; 
        }
    public void setMethods(List<MethodResult> methods) {
         this.methods = methods;
         }
}
