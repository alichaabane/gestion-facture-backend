package com.sesame.gestionfacture.entity;

public enum Role {

    SUPERADMIN{
        @Override
        public String toString() {
            return "SUPERADMIN";
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
