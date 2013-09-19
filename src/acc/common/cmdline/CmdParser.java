package acc.common.cmdline;

import acc.common.cmdline.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements parser and dispatcher for command line arguments.
 */
public class CmdParser {
    /**
     * Line separator string (platform independent).
     */
    private static final String LineSeparator = System.getProperty("line.separator");
    
    /**
     * Object to which the command line arguments will be dispatched to.
     */
    private Object _impl;

    /**
     * List of available commands.
     */
    private List<Command> _commands;

    /**
     * Default command.
     */
    private Command _defaultCommand;

    /**
     * List of global options available in each command.
     */
    private List<GlobalParameter> _globalParameters;

    /**
     * Constructor of the parser class.
     * @param impl Object to which the command line arguments will be dispatched to
     * @throws CmdException Thrown when the definition of command line is invalid
     */
    public CmdParser(Object impl) throws CmdException {
        this._impl = impl;
        this._commands = new ArrayList<Command>();
        this._globalParameters = new ArrayList<GlobalParameter>();
        initParser(impl.getClass());
    }

    /**
     * Iterates over the methods and fields of the specified class and initializes the parser.
     * @param type Class with annotations defining the command line
     * @throws CmdException Thrown when the definition of command line is invalid
     */
    private void initParser(Class type) throws CmdException {
        for (Method method : type.getDeclaredMethods()) {
            this.addCommandFromMethod(method);
        }

        for (Field field : type.getFields()) {
            GlobalParameter parameter = new GlobalParameter();
            parameter.Field = field;
            parameter.Parameter = this.createParameterForCommand(field.getType(), field.getAnnotations());
            this._globalParameters.add(parameter);
        }
    }

    /**
     * Explores annotations of the specified method and based on that add a command to the command list.
     * @param method Method to explore
     * @throws CmdException Thrown when the definition of command line is invalid
     */
    private void addCommandFromMethod(Method method) throws CmdException {
        Command command = new Command();
        command.Name = method.getName().toLowerCase();
        command.Method = method;

        Annotation annotation = method.getAnnotation(Name.class);
        if (annotation != null) {
            Name nameAnnotation = (Name)annotation;
            if (nameAnnotation.name() != null) {
                command.Name = nameAnnotation.name();
            }
            command.ShortName = nameAnnotation.shortName();
            command.Description = nameAnnotation.description();
        }

        annotation = method.getAnnotation(Help.class);
        if (annotation != null) {
            command.IsHelp = true;
        }

        int paramCount = method.getGenericParameterTypes().length;
        Class[] paramTypes = method.getParameterTypes();
        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        command.Parameters = new ArrayList<Parameter>(paramCount);
        for (int i = 0; i < paramCount; i++) {
            command.Parameters.add(this.createParameterForCommand(paramTypes[i], paramAnnotations[i]));
        }

        if (this.findCommand(command.Name) != null || this.findCommand(command.ShortName) != null) {
            throw new CmdException(CmdExceptionCode.PARSE_DUPLICATE_COMMAND_NAME, command.Name, "Duplicate command names are not allowed");
        }
        this._commands.add(command);

        annotation = method.getAnnotation(DefaultCommand.class);
        if (annotation != null) {
            if (this._defaultCommand != null) {
                throw new CmdException(CmdExceptionCode.PARSE_DUPLICATE_DEFAULT_COMMAND, command.Name, "Only one default command is allowed");
            }
            this._defaultCommand = command;
        }
    }

    /**
     * Creates a Parameter object for a command based on the parameter type and parameter annotations.
     * @param parameterType Type of the parameter
     * @param annotations Annotations of the parameter
     * @return Parameter object
     * @throws CmdException Thrown when the definition of command line is invalid
     */
    private Parameter createParameterForCommand(Class parameterType, Annotation[] annotations) throws CmdException {
        Parameter param = new Parameter();
        param.Type = parameterType;
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() == Name.class) {
                Name name = (Name)annotation;
                param.Name = name.name();
                param.ShortName = name.shortName();
                param.Description = name.description();
            }
            else if (annotation.annotationType() == DefaultValue.class) {
                DefaultValue defaultValue = (DefaultValue)annotation;
                param.DefaultValue = defaultValue.value();
            }
            else if (annotation.annotationType() == Required.class) {
                param.IsRequired = true;
            }
            else if (annotation.annotationType() == NonEmpty.class) {
                param.Validator = new NonEmptyValidator();
            }
            else if (annotation.annotationType() == Regex.class) {
                Regex regex = (Regex)annotation;
                param.Validator = new RegexValidator(regex.value());
            }
            else if (annotation.annotationType() == LongRange.class) {
                LongRange range = (LongRange)annotation;
                param.Validator = new LongValidator(range.min(), range.max());
            }
            else if (annotation.annotationType() == DoubleRange.class) {
                DoubleRange range = (DoubleRange)annotation;
                param.Validator = new DoubleValidator(range.min(), range.max());
            }
        }

        if (param.Name == null) {
            throw new CmdException(CmdExceptionCode.PARSE_PARAM_NAME_UNDEFINED, "Parameter name must be defined");
        }

        return param;
    }

    /**
     * Helper method showing how to use CmdParser class.
     * @param args Command line arguments
     * @param impl Object to which the command line arguments will be dispatched to
     */
    public static void dispatchArgs(String[] args, Object impl) {
        try {
            CmdParser cmdParser = new CmdParser(impl);
            cmdParser.dispatch(args);
        }
        catch (CmdException e) {
            System.err.printf("Invalid command: %s (%s)%n", e.getMessage(), e.getInvalidArgument());
        }
    }

    /**
     * Invokes a proper method based on the command line arguments.
     * @param args Command line arguments
     * @throws CmdException Thrown when the definition of command line is invalid
     */
    public void dispatch(String[] args)
            throws CmdException {
        Command command = null;
        Map<String, Option> options = new HashMap<String, Option>();
        for (String arg : args) {
            if (arg.startsWith("-")) {
                // Option
                Option option = this.parseOption(arg);
                options.put(option.Name, option);
            }
            else {
                // Command
                if (command != null) {
                    throw new CmdException(CmdExceptionCode.DISPATCH_DUPLICATE_COMMAND, arg, "The command line can only have one command");
                }

                String commandName = arg.toLowerCase();
                command = this.findCommand(commandName);
                if (command == null) {
                    throw new CmdException(CmdExceptionCode.DISPATCH_UNKNOWN_COMMAND, arg, "Unknown command");
                }
            }
        }

        if (command == null && this._defaultCommand == null) {
            throw new CmdException(CmdExceptionCode.DISPATCH_NO_COMMAND, "No command was specified");
        }
        else if (command == null) {
            command = this._defaultCommand;
        }

        for (Option option : options.values()) {
            boolean found = false;
            for (GlobalParameter globalParameter : this._globalParameters) {
                if (globalParameter.Parameter.Name.equals(option.Name) || globalParameter.Parameter.ShortName.equals(option.Name)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                for (Parameter parameter : command.Parameters) {
                    if (parameter.Name.equals(option.Name) || parameter.ShortName.equals(option.Name)) {
                        found = true;
                        break;
                    }
                }
            }

            if (!found) {
                throw new CmdException(CmdExceptionCode.DISPATCH_UNKNOWN_PARAMETER, option.Name, "Unknown option");
            }
        }

        ArrayList<Object> commandArgs = new ArrayList<Object>();
        for (Parameter parameter : command.Parameters) {
            Object value;
            if (options.containsKey(parameter.Name)) {
                // Parameter specified using full name, good!
                Option option = options.get(parameter.Name);
                value = getParameterValue(parameter.Type, parameter.Name, option.Value);
            }
            else if (options.containsKey(parameter.ShortName)) {
                // Parameter specified using short name, good!
                Option option = options.get(parameter.ShortName);
                value = getParameterValue(parameter.Type, parameter.ShortName, option.Value);
            }
            else if (parameter.IsRequired) {
                // Parameter not specified, but it's required!
                throw new CmdException(CmdExceptionCode.DISPATCH_MISSING_REQUIRED_PARAMETER, parameter.Name, "Missing required parameter");
            }
            else {
                // Parameter not specified, but it was not required. Using default value defined for parameter or for a type
                if (parameter.DefaultValue != null) {
                    value = getParameterValue(parameter.Type, parameter.Name, parameter.DefaultValue);
                }
                else {
                    value = getDefaultValue(parameter.Type);
                }
            }
            this.validate(value, parameter);
            commandArgs.add(value);
        }

        this.runCommand(command, commandArgs.toArray(), options);
    }

    /**
     * Parses a command line option.
     * @param option A command line option
     * @return An Option object
     */
    private Option parseOption(String option) {
        if (option.startsWith("--")) {
            option = option.substring(2);
        }
        else if (option.startsWith("-")) {
            option = option.substring(1);
        }

        int separatorIndex = option.indexOf(':');
        Option result = new Option();
        if (separatorIndex > 0) {
            result.Name = option.substring(0, separatorIndex);
            if (option.length() > separatorIndex + 1) {
                result.Value = option.substring(separatorIndex + 1);
            }
            else {
                result.Value = "";
            }
        }
        else {
            result.Name = option;
        }

        return result;
    }

    /**
     * Finds a command on a command list based on the full or short name.
     * @param commandName Full or short name of the command
     * @return An object of the matching command, null if not found
     */
    private Command findCommand(String commandName) {
        if (commandName != null) {
            for (Command command : this._commands) {
                if ((command.Name != null && command.Name.equals(commandName))
                        || (command.ShortName != null && command.ShortName.equals(commandName))) {
                    return command;
                }
            }
        }

        return null;
    }

    /**
     * Validates whether the specified value is correct for specified parameter (if validator is present).
     * @param value Value to check
     * @param parameter Parameter definition
     * @throws CmdException Thrown when the value is not correct
     */
    private void validate(Object value, Parameter parameter) throws CmdException {
        if (parameter.Validator != null) {
            String errorMessage = parameter.Validator.validateValue(value);
            if (errorMessage != null) {
                throw new CmdException(CmdExceptionCode.DISPATCH_VALIDATION_ERROR, parameter.Name, errorMessage);
            }
        }
    }

    /**
     * Runs a specified command by invoking a corresponding method.
     * @param command Command to run
     * @param arguments Command's arguments
     * @param options Map with options (needed to set global parameters)
     * @throws CmdException Thrown when the method could not be invoked for some reason
     */
    private void runCommand(Command command, Object[] arguments, Map<String, Option> options) throws CmdException {
        try {
            if (command.IsHelp) {
                command.Method.invoke(this._impl, this.getHelpText());
                return;
            }

            for (GlobalParameter globalParameter : this._globalParameters) {
                Object value;
                if (options.containsKey(globalParameter.Parameter.Name)) {
                    // Parameter specified using full name, good!
                    Option option = options.get(globalParameter.Parameter.Name);
                    value = getParameterValue(globalParameter.Parameter.Type, globalParameter.Parameter.Name, option.Value);
                }
                else if (options.containsKey(globalParameter.Parameter.ShortName)) {
                    // Parameter specified using short name, good!
                    Option option = options.get(globalParameter.Parameter.ShortName);
                    value = getParameterValue(globalParameter.Parameter.Type, globalParameter.Parameter.ShortName, option.Value);
                }
                else if (globalParameter.Parameter.IsRequired) {
                    // Parameter not specified, but it's required!
                    throw new CmdException(CmdExceptionCode.DISPATCH_MISSING_REQUIRED_PARAMETER, globalParameter.Parameter.Name, "Missing required parameter");
                }
                else {
                    // Parameter not specified, but it was not required. Using default value defined for parameter or for a type
                    if (globalParameter.Parameter.DefaultValue != null) {
                        value = getParameterValue(globalParameter.Parameter.Type, globalParameter.Parameter.Name, globalParameter.Parameter.DefaultValue);
                    }
                    else {
                        value = getDefaultValue(globalParameter.Parameter.Type);
                    }
                }
                this.validate(value, globalParameter.Parameter);
                globalParameter.Field.set(this._impl, value);
            }

            command.Method.invoke(this._impl, arguments);
        } catch (CmdException e) {
            throw e;
        } catch (Exception e) {
            throw new CmdException(CmdExceptionCode.DISPATCH_INVOKE_ERROR, "Unable to invoke command", e);
        }
    }

    /**
     * Generates a help text based on the parsed data.
     * @return Help text with overall usage
     */
    private String getHelpText() {
        StringBuilder builder = new StringBuilder();
        builder.append("Usage: java MainClass <command> [options...]"); builder.append(LineSeparator);
        builder.append(LineSeparator);

        builder.append("Global options:"); builder.append(LineSeparator);
        for (GlobalParameter parameter : this._globalParameters) {
            builder.append("  ");
            builder.append(formatParameter(parameter.Parameter, 18));
            builder.append(LineSeparator);
        }
        builder.append(LineSeparator);

        builder.append("Commands:"); builder.append(LineSeparator);
        for (Command command : this._commands) {
            builder.append(this.formatCommand(command));
        }
        return builder.toString();
    }

    /**
     * Generates a help message for a command.
     * @param command Command to generate help for
     * @return Help text about command
     */
    private String formatCommand(Command command) {
        StringBuilder builder = new StringBuilder();
        builder.append("  ");
        builder.append(command.Name);
        if (command.ShortName != null && command.ShortName.length() > 0) {
            builder.append(" (");
            builder.append(command.ShortName);
            builder.append(")");
        }
        while (builder.length() < 20) {
            builder.append(' ');
        }
        builder.append(command.Description);
        builder.append(LineSeparator);
        for (Parameter parameter : command.Parameters) {
            builder.append("    ");
            builder.append(this.formatParameter(parameter, 20));
            builder.append(LineSeparator);
        }
        builder.append(LineSeparator);

        return builder.toString();
    }

    /**
     * Generates help text for a parameter.
     * @param parameter Parameter to generate help text for
     * @param paramSize The size of the parameter block (for better alignment)
     * @return Help text for a parameter
     */
    private String formatParameter(Parameter parameter, int paramSize) {
        StringBuilder builder = new StringBuilder();
        if (parameter.Name != null && parameter.Name.length() > 0) {
            builder.append("--");
            builder.append(parameter.Name);
        }
        if (parameter.ShortName != null && parameter.ShortName.length() > 0) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append("-");
            builder.append(parameter.ShortName);
        }
        while (builder.length() < paramSize) {
            builder.append(' ');
        }
        builder.append(parameter.Description);
        builder.append(" [");
        builder.append(parameter.Type.getSimpleName());
        if (parameter.IsRequired) {
            builder.append(", Required");
        }
        if (parameter.DefaultValue != null && parameter.DefaultValue.length() > 0) {
            builder.append(", Default=");
            builder.append(parameter.DefaultValue);
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * Parses the string value to a proper parameter type.
     * @param clazz Parameter type
     * @param name Name of the parameter (to provide eventual error information)
     * @param value String value to parse
     * @return Object of the proper type with parsed value
     * @throws CmdException Thrown when parsing the value to a specified type failed
     */
    private static Object getParameterValue(Class clazz, String name, String value) throws CmdException {
        if (clazz.equals(String.class) && value != null) {
            return value;
        } else if (clazz.equals(boolean.class)) {
            if (value != null) {
                return Boolean.parseBoolean(value);
            }
            else {
                return Boolean.TRUE;
            }
        } else if (value == null) {
            throw new CmdException(CmdExceptionCode.DISPATCH_EMPTY_PARAMETER, name, "Parameter cannot be empty");
        } else if (clazz.equals(byte.class)) {
            return Byte.parseByte(value);
        } else if (clazz.equals(short.class)) {
            return Short.parseShort(value);
        } else if (clazz.equals(int.class)) {
            return Integer.parseInt(value);
        } else if (clazz.equals(long.class)) {
            return Long.parseLong(value);
        } else if (clazz.equals(float.class)) {
            return Float.parseFloat(value);
        } else if (clazz.equals(double.class)) {
            return Double.parseDouble(value);
        } else {
            throw new CmdException(CmdExceptionCode.DISPATCH_UNSUPPORTED_PARAMETER_TYPE, clazz.getSimpleName(), "Unsupported parameter type");
        }
    }

    /**
     * Returns a default value for a specified type.
     * @param clazz Type for which a default value should be returned
     * @return Default value for a specified type
     */
    private static Object getDefaultValue(Class clazz) {
        if (clazz.equals(boolean.class)) {
            return Boolean.FALSE;
        } else if (clazz.equals(byte.class)) {
            return (byte) 0;
        } else if (clazz.equals(short.class)) {
            return (short) 0;
        } else if (clazz.equals(int.class)) {
            return 0;
        } else if (clazz.equals(long.class)) {
            return (long) 0;
        } else if (clazz.equals(float.class)) {
            return (float) 0;
        } else if (clazz.equals(double.class)) {
            return (double) 0;
        } else {
            return null;
        }
    }

    /**
     * Helper class holding command information.
     */
    private class Command {
        public String Name;
        public String ShortName;
        public String Description;
        public ArrayList<Parameter> Parameters;
        public boolean IsHelp;
        public Method Method;
    }

    /**
     * Helper class holding command parameter information.
     * Options are taken from command line, parameters from annotations.
     */
    private class Parameter {
        public String Name;
        public String ShortName;
        public String Description;
        public String DefaultValue;
        public boolean IsRequired;
        public Class Type;
        public IValidator Validator;
    }

    /**
     * Helper class holding global parameter information.
     * Options are taken from command line, parameters from annotations.
     */
    private class GlobalParameter {
        Parameter Parameter;
        Field Field;
    }

    /**
     * Helper class holding option information.
     * Options are taken from command line, parameters from annotations.
     */
    private class Option {
        public String Name;
        public String Value;
    }
}
