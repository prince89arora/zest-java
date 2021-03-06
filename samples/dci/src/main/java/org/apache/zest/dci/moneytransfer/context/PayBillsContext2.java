/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package org.apache.zest.dci.moneytransfer.context;

import java.util.ArrayList;
import java.util.List;
import org.apache.zest.api.injection.scope.Structure;
import org.apache.zest.api.unitofwork.UnitOfWorkFactory;
import org.apache.zest.dci.moneytransfer.domain.data.BalanceData;

/**
 * Context for paying bills from an account to a list of creditor accounts.
 * <p>
 * Roles are defined within the context.
 * A RoleMap lists what Roles an entity can play.
 * </p>
 */
public class PayBillsContext2
{
    @Structure
    UnitOfWorkFactory uowf;

    private SourceAccountRole sourceAccount = new SourceAccountRole();

    public PayBillsContext2 bind( BalanceData account )
    {
        sourceAccount.bind( account );
        return this;
    }

    public void payBills()
        throws Exception
    {
        sourceAccount.payBills();
    }

    /**
     * The SourceAccountRole orchestrates the Pay Bills use case interactions.
     * <p>
     * Code matches the use case text carefully (see references below).
     * </p>
     * <p>
     * Pay Bills use case scenario:
     * </p>
     * <p>
     * 1) Bank finds creditors (could be a use case scenario in itself)
     * </p>
     * <p>
     * 2) Bank calculates the amount owed to creditors
     * </p>
     * <p>
     * 3) Bank verifies sufficient funds
     * </p>
     * <p>
     * 4) Bank transfer money to each creditor
     * </p>
     * <p>
     * Algorithm (steps to implement the scenario):
     * </p>
     * <p>
     * <p>
     * 1a) Source Account finds list of creditors
     * </p>
     * <p>
     * 2a) Source Account loops creditors to find the sum owed
     * </p>
     * <p>
     * 3a) Source Account verifies that its current balance is greater than the sum owed, and throws an exception if not
     * </p>
     * <p>
     * 4a) Source Account loops creditors
     * <p>
     * 4b) Make a MoneyTransfer of the amount owed to each creditor
     * </p>
     */
    class SourceAccountRole
        extends Role<BalanceData>
    {
        void payBills()
            throws IllegalArgumentException
        {
            List<BalanceData> creditors = getCreditors();                                             // 1a

            Integer sumOwed = getSumOwedTo( creditors );                                              // 2a

            if( self.getBalance() - sumOwed < 0 )                                                      // 3a
            {
                throw new IllegalArgumentException( "Insufficient funds to pay bills." );
            }

            final TransferMoneyContext2 transferMoney = new TransferMoneyContext2();
            for( BalanceData creditor : creditors )                                                    // 4a
            {
                if( creditor.getBalance() < 0 )
                {
                    final Integer amountOwed = -creditor.getBalance();

                    // Bind nested context and execute enactment
                    transferMoney.bind( self, creditor ).transfer( amountOwed );                      // 4b
                }
            }
        }

        // Internal helper methods to make the code above more readable / comparable to the algorithm text

        private List<BalanceData> getCreditors()
        {
            // Creditor retrieval could be a use case in itself...
            List<BalanceData> creditors = new ArrayList<BalanceData>();
            creditors.add( uowf.currentUnitOfWork().get( BalanceData.class, "BakerAccount" ) );
            creditors.add( uowf.currentUnitOfWork().get( BalanceData.class, "ButcherAccount" ) );
            return creditors;
        }

        private Integer getSumOwedTo( List<BalanceData> creditors )
        {
            Integer sumOwed = 0;
            for( BalanceData creditor : creditors )
            {
                sumOwed += creditor.getBalance() < 0 ? -creditor.getBalance() : 0;
            }

            return sumOwed;
        }
    }
}