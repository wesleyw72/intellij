/*
 * Copyright 2020 The Bazel Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.idea.blaze.base.util;

import com.google.idea.blaze.base.io.VfsUtils;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import java.io.File;

/**
 * Helper to fix path for file from Mac Catalina platform
 *
 * <p>b/152076083. In Mac Catalina platform /google/src is a symbolic to /Volumes/google/src. So if
 * {@link File#getCanonicalPath()} or {@link VirtualFile#getCanonicalPath()} is used, user will get
 * path to "/Volumes/google/src/...". This path cannot pass many prefix check e.g. {@link
 * com.google.idea.blaze.google3.wizard.SrcfsConstants#SRCFS_HEAD_PATH}. And if user cannot modify
 * such file since they are considered as out of project files. Even update {@link
 * com.google.idea.blaze.base.model.primitives.WorkspaceRoot#directory} can solve this issue, it
 * will lead to existed project tree changed and many potential issues. So a utils class is provided
 * to remove Catalina file paths that prefix "/Volumes" to make path the same as all other platforms
 */
public class PathUtil {
  private static final String VOLUMES = "/Volumes";
  private static final String CATALINA_SRCFS_ABSOLUTE_PATH_PREFIX = "/Volumes/google/src";
  private static final String SRCFS_ABSOLUTE_PATH_PREFIX = "/google/src";

  public static String fix(String path) {
    if (!SystemInfo.isMacOSCatalina) {
      return path;
    }
    return path.replaceAll(CATALINA_SRCFS_ABSOLUTE_PATH_PREFIX, SRCFS_ABSOLUTE_PATH_PREFIX);
  }

  public static VirtualFile fix(VirtualFile virtualFile) {
    if (!SystemInfo.isMacOSCatalina) {
      return virtualFile;
    }
    String path = virtualFile.getPath();
    if (!path.startsWith(VOLUMES)) {
      return virtualFile;
    }
    path = path.substring(VOLUMES.length());
    return VfsUtils.resolveVirtualFile(new File(path), true);
  }

  public static File fix(File file) {
    if (!SystemInfo.isMacOSCatalina) {
      return file;
    }
    String path = file.getPath();
    if (!path.startsWith(VOLUMES)) {
      return file;
    }
    path = path.substring(VOLUMES.length());
    return new File(path);
  }

  private PathUtil() {}
}
