/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio;

public enum PortfolioProductType {

    LOAN(1, "productType.loan"), //
    SAVING(2, "productType.saving"), //
    CLIENT(5, "productType.client"), //
    PROVISIONING(3, "productType.provisioning"), //
    SHARES(4, "productType.shares"), //
    CURRENT_ACCOUNT(6, "productType.currentAccount"); // NEW: Our addition - ID 6, code for Current Account (hardcodes it like the others)

    private final Integer value;
    private final String code;

    PortfolioProductType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    @Override
    public String toString() {
        return name().toString().replaceAll("_", " ");
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return this.code;
    }

    public static PortfolioProductType fromInt(final Integer v) {
        if (v == null) {
            return null;
        }

        switch (v) {
            case 1:
                return LOAN;
            case 2:
                return SAVING;
            case 3:
                return CLIENT;
            case 4:
                return PROVISIONING;
            case 5:
                return SHARES;
            case 6:
                return CURRENT_ACCOUNT;
            default:
                return null;
        }
    }

    public boolean isSavingProduct() {
        return this.equals(SAVING);
    }

    public boolean isLoanProduct() {
        return this.equals(LOAN);
    }

    public boolean isClient() {
        return this.equals(CLIENT);
    }

    public boolean isShareProduct() {
        return this.equals(SHARES);
    }

    public boolean isCurrentAccountProduct() {
        return this.equals(CURRENT_ACCOUNT);
    }
}
