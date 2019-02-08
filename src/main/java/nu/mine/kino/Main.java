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
//作成日: 2019/02/08

package nu.mine.kino;

import lombok.extern.slf4j.Slf4j;
import nu.mine.kino.web.JWTUtils;

/**
 * @author Masatomi KINO
 * @version $Revision$
 */
@Slf4j
public class Main {
    public static void main(String[] args) {
        // https://gist.github.com/masatomix/d384010156b8c16881fcd0dccd315ef5
        String id_token = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImIyZTQ2MGZmM2EzZDQ2ZGZlYzcyNGQ4NDg0ZjczNDc2YzEzZTIwY2YiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20veHh4eHh4LXNhbXBsZXMiLCJhdWQiOiJ4eHh4eHgtc2FtcGxlcyIsImF1dGhfdGltZSI6MTU0OTU5MzgxMCwidXNlcl9pZCI6IlVOWHBENDBZNUVZZkt4eHh4eHh4Iiwic3ViIjoiVU5YcEQ0MFk1RVlmS3h4eHh4eHgiLCJpYXQiOjE1NDk1OTM4MTAsImV4cCI6MTU0OTU5NzQxMCwiZW1haWwiOiJob2dlaG9nZUBleGFtcGxlLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwiZmlyZWJhc2UiOnsiaWRlbnRpdGllcyI6eyJlbWFpbCI6WyJob2dlaG9nZUBleGFtcGxlLmNvbSJdfSwic2lnbl9pbl9wcm92aWRlciI6InBhc3N3b3JkIn19Cg.D3ZDm-UPDGRqezz6Q9QKpZtSVovxlWZNt-pyArLlruo1DqfkmaYkXuTjMpxIbB0sCySDAme3ZeenRYxgBrQJhqZiwFx_mTNfjNoQSUVbRjLrSYdtXLBtgy6OvJGsN93UfFfhb2kAeBjDtOPTE6WOWyJ7wDRK0bmkYvYLZ9NMgFsc9-ELfqew7jOVnZTsem3dwkhfQ-_qHJnRD7xkLmEu2CA0yUSbajVwy-rDpC5eRVZVjnnFpgghJckBpQTdxXesM58aRF5uiSLsIi6KYimDyqV_cQL_oAojW0fR-X-Q0GqD4FYsGmk1hMy-n5ClOUmCKvHLcN6tAWQKScdvYAx3cA\r\n";
        boolean checkIdToken = JWTUtils.checkIdToken(id_token);
        log.debug("{}", checkIdToken);
    }
}
