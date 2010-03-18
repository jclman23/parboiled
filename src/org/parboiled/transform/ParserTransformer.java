/*
 * Copyright (C) 2009 Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.parboiled.transform;

import org.jetbrains.annotations.NotNull;

import static org.parboiled.transform.AsmUtils.findLoadedClass;
import static org.parboiled.transform.AsmUtils.getExtendedParserClassName;

public class ParserTransformer {

    private ParserTransformer() {}

    public static Class<?> transformParser(@NotNull Class<?> parserClass) throws Exception {
        synchronized (parserClass.getClassLoader()) {
            // first check whether we did not already create and load the extension of the given parser class
            Class<?> extendedClass = findLoadedClass(
                    getExtendedParserClassName(parserClass.getName()), parserClass.getClassLoader()
            );
            if (extendedClass != null) return extendedClass;

            // not loaded yet, so create
            ClassTransformer transformer = createClassTransformer();
            ParserClassNode classNode = transformer.transform(new ParserClassNode(parserClass));
            return classNode.extendedClass;
        }
    }

    static ClassTransformer createClassTransformer() {
        return new ClassNodeInitializer(
                new ActionMethodRewritingTransformer(createActionMethodTransformer(),
                        new ConstructorGenerator(
                                new WithCallToSuperReplacer(
                                        new LabelApplicator(
                                                new LeafApplicator(
                                                        new CachingGenerator(
                                                                new ParserClassDefiner()
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

    static MethodTransformer createActionMethodTransformer() {
        return new LineNumberRemover(
                new ReturnInstructionUnifier(
                        new InstructionGraphCreator(
                                new InstructionGraphPartitioner(
                                        new RuleMethodRewriter()
                                )
                        )
                )
        );
    }

}