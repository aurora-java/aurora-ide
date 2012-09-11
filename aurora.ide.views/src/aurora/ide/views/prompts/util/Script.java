package aurora.ide.views.prompts.util;

public class Script {

	public static final String PROMPT_CODE = "%PROMPT_CODE%";

	public static final String LANG = "%LANG%";
	public static final String TEXT = "%TEXT%";

	public static final String DeletePromptCode = "sys_prompt_pkg.delete_prompt('%PROMPT_CODE%'); ";
	public static final String InsertZHSPrompt = "sys_prompt_pkg.sys_prompts_load('%PROMPT_CODE%', 'ZHS', '%TEXT%');";

	//
	//
}
