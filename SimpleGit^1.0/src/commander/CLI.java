package commander;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;


import core.*;
import gitobject.Commit;
import repository.Repository;



public class CLI {	
	
	/**
	 * Command 'jit init'
	 * @param args
	 * @throws IOException
	 */
	public static void jitInit(String[] args) throws IOException {
		String path = "";
		if(args.length <= 2) { //get default working path
			path = new File(".").getCanonicalPath();
			JitInit.init(path);
		}else if(args[2].equals("-help")){ //see help
			System.out.println("usage: jit init [<path>] [-help]\r\n" +
					"\r\n" +
					"jit init [<path>]:	Create an empty jit repository or reinitialize an existing one in the path or your default working directory.");
		}else {
			path = args[2];
			if(!new File(path).isDirectory()) { //if the working path input is illegal
				System.out.println(path + "is not a legal directory. Please init your reposiroty again. See 'jit init -help'.");
			}else {
				JitInit.init(path);
			}
		}
	}
	

	/**
	 * Command 'jit help'.
	 */
	public static void jitHelp() {
		System.out.println("usage: jit [--version] [--help] [-C <path>] [-c name=value]\r\n" +
				"           [--exec-path[=<path>]] [--html-path] [--man-path] [--info-path]\r\n" +
				"           [-p | --paginate | --no-pager] [--no-replace-objects] [--bare]\r\n" +
				"           [--git-dir=<path>] [--work-tree=<path>] [--namespace=<name>]\r\n" +
				"           <command> [<args>]\r\n" +
				"\r\n" +
				"These are common Jit commands used in various situations:\r\n" +
				"\r\n" +
				"start a working area\r\n" +
				"   init       Create an empty Jit repository or reinitialize an existing one\r\n" +
				"\r\n" +
				"work on the current change\r\n" +
				"   add        Add file contents to the index\r\n" +
				"   reset      Reset current HEAD to the specified state\r\n" +
				"   rm         Remove files from the working tree and from the index\r\n" +
				"\r\n" +
				"examine the history and state\r\n" +
				"   log        Show commit logs\r\n" +
				"   status     Show the working tree status\r\n" +
				"\r\n" +
				"grow, mark and tweak your common history\r\n" +
				"   branch     List, create, or delete branches\r\n" +
				"   checkout   Switch branches or restore working tree files\r\n" +
				"   commit     Record changes to the repository\r\n" +
				"   diff       Show changes between commits, commit and working tree, etc\r\n" +
				"   merge      Join two or more development histories together\r\n" +
				"\r\n" +
				"'jit help -a' and 'jit help -g' list available subcommands and some\r\n" +
				"concept guides. See 'jit help <command>' or 'jit help <concept>'\r\n" +
				"to read about a specific subcommand or concept.");
	}
	
	public static void main(String[] args) throws Exception {
		if(args.length <= 1 || args[1].equals("help")) {
			jitHelp();
		}else {
			if(args[1].equals("init")) {
				jitInit(args);
			} else {
				System.out.println("jit: " + args[1] + "is not a git command. See 'git help'.");
			}
		}
	}
}
