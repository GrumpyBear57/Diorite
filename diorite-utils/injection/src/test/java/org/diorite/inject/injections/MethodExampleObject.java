/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016. Diorite (by Bartłomiej Mazur (aka GotoFinal))
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.diorite.inject.injections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;

import org.diorite.inject.AfterInject;
import org.diorite.inject.BeforeInject;
import org.diorite.inject.EmptyAnn;
import org.diorite.inject.InjectableClass;
import org.diorite.inject.Injection;
import org.diorite.inject.NamedInject;
import org.diorite.inject.Provider;
import org.diorite.inject.Singleton;

@InjectableClass
public class MethodExampleObject
{
    private static final Collection<String> invoked_pattern = List.of("beforeMoreModules", "injectMoreModules", "afterMoreModules");
    private final        Collection<String> invoked         = new ArrayList<>(3);

    @NamedInject()
    @EmptyAnn
    @Singleton
    private Provider<Module> someModuleProvider = Injection.inject();

    public Provider<Module> getSomeModuleProvider()
    {
        return this.someModuleProvider;
    }

    @NamedInject
    @Singleton
    private void injectMoreModules(Module module1, @NamedInject("module1") Module module12, Module module2, @NamedInject("module2") Module module22,
                                   @EmptyAnn Module guard)
    {
        System.out.println("[MethodExampleObject] injectMoreModules: " + module1 + " & " + module12 + " & " + module2 + " & " + module22 + " & " + guard);
        Assert.assertEquals(module1.getName(), "Module1");
        Assert.assertEquals(module12.getName(), "Module1");
        Assert.assertEquals(module1.hashCode(), module12.hashCode());
        Assert.assertNotSame(module1, module12); // there are singletons, but each one is separate singleton.
        Assert.assertEquals(module2.getName(), "Module2");
        Assert.assertEquals(module22.getName(), "Module2");
        Assert.assertSame(module2, module22); // Module2 is bind to single object
        Assert.assertEquals(guard.getName(), "guard");
        this.invoked.add("injectMoreModules");
    }

    @AfterInject("moreModules")
    private void afterMoreModules()
    {
        System.out.println("[MethodExampleObject] afterMoreModules");
        this.invoked.add("afterMoreModules");
    }

    @BeforeInject("MoreModules")
    private void beforeMoreModules()
    {
        System.out.println("[MethodExampleObject] beforeMoreModules");
        this.invoked.add("beforeMoreModules");
    }

    public void assertInjections()
    {
        Assert.assertNotNull(this.someModuleProvider);
        Assert.assertEquals(invoked_pattern, this.invoked);
        Assert.assertEquals(this.someModuleProvider.getNotNull().getName(), "someModule");
    }

    public String toString()
    {
        return String.valueOf(this.someModuleProvider.get());
    }
}
