/*
 * Copyright (c) 2010, Rickard Öberg. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.qi4j.dci.moneytransfer.domain.entity;

import org.qi4j.api.entity.EntityComposite;
import org.qi4j.dci.moneytransfer.context.TransferMoneyContext;
import org.qi4j.dci.moneytransfer.domain.data.BalanceData;

/**
 * You can transfer money to and from a checking account
 */
public interface CheckingAccountEntity
   extends EntityComposite,
   // Data
      BalanceData
{}
