package icu.etl.springboot.starter.script;

import java.util.Map;

import icu.etl.ioc.EasyetlContext;
import icu.etl.ioc.EasyetlContextAware;
import icu.etl.script.UniversalScriptAnalysis;
import icu.etl.script.UniversalScriptContext;
import icu.etl.script.UniversalScriptFormatter;
import icu.etl.script.UniversalScriptSession;
import icu.etl.script.UniversalScriptStderr;
import icu.etl.script.UniversalScriptStdout;
import icu.etl.script.UniversalScriptVariable;
import icu.etl.script.compiler.ScriptAnalysis;
import org.springframework.context.ApplicationContext;

/**
 * @author jeremy8551@qq.com
 * @createtime 2023/10/25
 */
public class SpringScriptAnalysis extends ScriptAnalysis implements UniversalScriptAnalysis, EasyetlContextAware {

    /** 容器上下文信息 */
    protected EasyetlContext context;

    public void setContext(EasyetlContext context) {
        this.context = context;
    }

    public SpringScriptAnalysis() {
        super();
    }

    public String replaceVariable(UniversalScriptSession session, UniversalScriptContext context, String str, boolean escape) {
        if (str == null) {
            return str;
        }

        UniversalScriptStdout stdout = context.getStdout();
        UniversalScriptStderr stderr = context.getStderr();
        UniversalScriptVariable localVariable = context.getLocalVariable();
        UniversalScriptVariable globalVariable = context.getGlobalVariable();
        SpringEnvironmentMap springEnv = new SpringEnvironmentMap(this.context.getBean(ApplicationContext.class));
        UniversalScriptFormatter format = context.getFormatter();
        Map<String, Object> variables = session.getVariables();

        str = this.replaceSubCommand(session, context, stdout, stderr, str, false);
        str = this.replaceShellSpecialVariable(session, str, false);
        str = this.replaceShellVariable(str, localVariable, format, false, true);
        str = this.replaceShellVariable(str, globalVariable, format, false, true);
        str = this.replaceShellVariable(str, variables, format, false, true);
        str = this.replaceShellVariable(str, springEnv, format, false, true);
        return escape ? this.unescapeSQL(str) : str;
    }

    public String replaceShellVariable(UniversalScriptSession session, UniversalScriptContext context, String str, boolean removeQuote, boolean keepVariable, boolean evalInnerCmd, boolean escape) {
        if (str == null) {
            return str;
        }

        UniversalScriptStdout stdout = context.getStdout();
        UniversalScriptStderr stderr = context.getStderr();
        UniversalScriptVariable localVariable = context.getLocalVariable();
        UniversalScriptVariable globalVariable = context.getGlobalVariable();
        UniversalScriptFormatter format = context.getFormatter();
        Map<String, Object> variables = session.getVariables();

        if (evalInnerCmd) {
            str = this.replaceSubCommand(session, context, stdout, stderr, str, true);
        }

        str = this.replaceShellSpecialVariable(session, str, true);
        str = this.replaceShellVariable(str, localVariable, format, true, true);
        str = this.replaceShellVariable(str, globalVariable, format, true, true);
        str = this.replaceShellVariable(str, variables, format, true, keepVariable);
        if (escape) { // 一定要在替换完字符串中变量之后再执行 {@link #unescapeSQL(String)} 方法
            str = this.unescapeSQL(str);
        }
        return removeQuote ? this.unQuotation(str) : str;
    }

}
