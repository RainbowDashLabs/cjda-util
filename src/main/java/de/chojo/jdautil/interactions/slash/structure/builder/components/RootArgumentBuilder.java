/*
 *     SPDX-License-Identifier: AGPL-3.0-only
 *
 *     Copyright (C) 2022 RainbowDashLabs and Contributor
 */

package de.chojo.jdautil.interactions.slash.structure.builder.components;

import de.chojo.jdautil.interactions.slash.structure.builder.ArgumentBuilder;

public interface RootArgumentBuilder extends BuildableMetaBuilder {
    RootArgumentBuilder argument(ArgumentBuilder argument);
}
