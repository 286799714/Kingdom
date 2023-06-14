package com.maydaymemory.kingdom.core.command;

public class CommandMeta {
    private final String rootCommandName;
    private final String subCommandName;
    private final String requiredPermission;
    private final String subCommandDescription;
    private final ParameterMeta[] parameterMetas;

    public CommandMeta(String rootCommandName, String subCommandName, String requiredPermission, String subCommandDescription, ParameterMeta[] parameterMetas){
        this.rootCommandName = rootCommandName;
        this.subCommandName = subCommandName;
        this.requiredPermission = requiredPermission;
        this.subCommandDescription = subCommandDescription;
        this.parameterMetas = parameterMetas;
    }

    public String getRootCommandName() {
        return rootCommandName;
    }

    public String getSubCommandName() {
        return subCommandName;
    }

    public String getRequiredPermission() {
        return requiredPermission;
    }

    public String getSubCommandDescription() {
        return subCommandDescription;
    }

    public ParameterMeta[] getParameterMetas() {
        return parameterMetas;
    }
}
