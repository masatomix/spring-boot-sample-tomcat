/******************************************************************************
 * Copyright (c) 2014 Masatomi KINO and others. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *      Masatomi KINO - initial API and implementation
 * $Id$
 ******************************************************************************/

package nu.mine.kino.service;

import lombok.Data;

/**
 * @author Masatomi KINO
 * @version $Revision$
 */
@Data
public class Hello {
    private String id;

    private String name;

    private boolean isIsDone;

}
