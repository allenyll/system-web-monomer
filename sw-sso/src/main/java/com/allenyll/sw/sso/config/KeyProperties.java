package com.allenyll.sw.sso.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties("encrypt")
public class KeyProperties {
    public static final String PREFIX = "encrypt";
    private String key;
    private String salt = "deadbeef";
    private boolean failOnError = true;
    private KeyStore keyStore = new KeyStore();

    public KeyProperties() {
    }

    public boolean isFailOnError() {
        return this.failOnError;
    }

    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSalt() {
        return this.salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public KeyStore getKeyStore() {
        return this.keyStore;
    }

    public void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    public static class KeyStore {
        private Resource location;
        private String password;
        private String alias;
        private String secret;
        private String type = "jks";

        public KeyStore() {
        }

        public String getAlias() {
            return this.alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public Resource getLocation() {
            return this.location;
        }

        public void setLocation(Resource location) {
            this.location = location;
        }

        public String getPassword() {
            return this.password;
        }

        public String getType() {
            return this.type;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getSecret() {
            return this.secret == null ? this.password : this.secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}

