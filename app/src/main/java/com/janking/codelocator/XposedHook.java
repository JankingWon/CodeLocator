package com.janking.codelocator;

import android.app.Application;
import android.content.Context;

import com.bytedance.tools.codelocator.CodeLocator;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class XposedHook implements IXposedHookLoadPackage {

    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.packageName.equals(loadPackageParam.processName)) {
            // 仅操作主进程
            return;
        }
        XposedBridge.log("监测到" + loadPackageParam.packageName);
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {

            @Override
            public void afterHookedMethod(@NotNull MethodHookParam param) {
                try {
                    Method method = CodeLocator.class.getDeclaredMethod("init", Application.class);
                    method.setAccessible(true);
                    method.invoke(null, (Application)(param.thisObject));

                    method = CodeLocator.class.getDeclaredMethod("registerReceiver");
                    method.setAccessible(true);
                    method.invoke(null);
                } catch (Exception e) {
                    XposedBridge.log(e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeHookedMethod(@NotNull MethodHookParam param) {

            }
        });
    }
}
