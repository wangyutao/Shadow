package com.jpyy001.tools.dynamic.manager;

import com.jpyy001.tools.core.common.TargetPackage;
import com.jpyy001.tools.dynamic.host.FailedException;
import com.jpyy001.tools.dynamic.host.NotFoundException;

public interface UuidManagerImpl {
    TargetPackage getPlugin(String uuid, String partKey) throws NotFoundException, FailedException;

    TargetPackage getPluginLoader(String uuid) throws NotFoundException, FailedException;

    TargetPackage getRuntime(String uuid) throws NotFoundException, FailedException;
}
