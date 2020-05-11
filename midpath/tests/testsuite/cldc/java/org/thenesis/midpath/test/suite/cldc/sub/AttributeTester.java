package org.thenesis.midpath.test.suite.cldc.sub;

public class AttributeTester {
    
    private int privateNumber = 11;
    public int publicNumber = 32;
    int packagePrivateNumber = 78;
    protected int protectedNumber = 85;
    
    public AttributeTester() {
        
    }
    
    protected AttributeTester(int value) {
        privateNumber += value;
        publicNumber += value;
        packagePrivateNumber += value;
        protectedNumber += value;
    }
    
    public int publicMethod() {
        return publicNumber;
    }
    
    int packagePrivateMethod() {
        return packagePrivateNumber;
    }
    
    protected int protectedMethod() {
        return protectedNumber;
    }

}
