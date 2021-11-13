package ilia.nemankov.controller;

import org.jboss.naming.remote.client.InitialContextFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public class ContextProviderImpl implements ContextProvider {

    public Context getContext() throws NamingException {
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactory.class.getName());
        props.put(Context.PROVIDER_URL, System.getenv("LAB2_PROVIDER_URL"));

        return new InitialContext(props);
    }

}
