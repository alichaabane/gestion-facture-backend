package com.sesame.gestionfacture.entity;

public enum Role {

    UTILISATEUR{
        @Override
        public String toString() {
            return "UTILISATEUR";
        }
    },
    ADMIN
            {
                @Override
                public String toString() {
                    return "ADMIN";
                }
            }
}
