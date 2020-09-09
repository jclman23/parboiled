package org.parboiled.examples.time;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.common.StringUtils;
import static org.parboiled.support.ParseTreeUtils.printNodeTree;
import org.parboiled.support.ParsingResult;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        TimeParser parser = Parboiled.createParser(TimeParser.class);
        
        while (true) {
            System.out.print("Enter a time expression (hh:mm(:ss)?, hh(mm(ss)?)? or h(mm)?, single RETURN to exit)!\n");
            String input = new Scanner(System.in).nextLine();
            if (StringUtils.isEmpty(input)) break;

            ParsingResult<?> result = new RecoveringParseRunner(parser.Time()).run(input);

            System.out.println(input + " = " + result.parseTreeRoot.getValue() + '\n');
            System.out.println(printNodeTree(result) + '\n');

            if (!result.matched) {
                System.out.println(StringUtils.join(result.parseErrors, "---\n"));
            }
        }
        
    }

}
