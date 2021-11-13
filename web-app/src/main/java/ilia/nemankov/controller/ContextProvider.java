package ilia.nemankov.controller;

import javax.naming.Context;
import javax.naming.NamingException;

public interface ContextProvider {

    Context getContext() throws NamingException;

}
