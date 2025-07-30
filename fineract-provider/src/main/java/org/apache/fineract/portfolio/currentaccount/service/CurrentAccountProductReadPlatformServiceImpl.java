// File: fineract-provider/src/main/java/org/apache/fineract/portfolio/currentaccount/service/CurrentAccountProductReadPlatformServiceImpl.java

package org.apache.fineract.portfolio.currentaccount.service;

import org.apache.fineract.infrastructure.core.service.database.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.organisation.monetary.service.CurrencyReadPlatformService;
import org.apache.fineract.portfolio.currentaccount.data.CurrentAccountProductData;
import org.apache.fineract.portfolio.currentaccount.exception.CurrentAccountProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Implementation of CurrentAccountProductReadPlatformService
 * This class uses JDBC for database operations for better performance in read operations
 */
@Service
public class CurrentAccountProductReadPlatformServiceImpl implements CurrentAccountProductReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;
    private final CurrencyReadPlatformService currencyReadPlatformService;

    @Autowired
    public CurrentAccountProductReadPlatformServiceImpl(final RoutingDataSource dataSource,
                                                        final PlatformSecurityContext context,
                                                        final CurrencyReadPlatformService currencyReadPlatformService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.context = context;
        this.currencyReadPlatformService = currencyReadPlatformService;
    }

    @Override
    public Collection<CurrentAccountProductData> retrieveAll() {
        this.context.authenticatedUser();

        final CurrentAccountProductMapper mapper = new CurrentAccountProductMapper();
        final String sql = "SELECT " + mapper.schema() + " WHERE cap.is_active = 1 ORDER BY cap.name";

        return this.jdbcTemplate.query(sql, mapper);
    }

    @Override
    public CurrentAccountProductData retrieveOne(final Long productId) {
        try {
            this.context.authenticatedUser();

            final CurrentAccountProductMapper mapper = new CurrentAccountProductMapper();
            final String sql = "SELECT " + mapper.schema() + " WHERE cap.id = ? AND cap.is_active = 1";

            return this.jdbcTemplate.queryForObject(sql, mapper, new Object[]{productId});

        } catch (final EmptyResultDataAccessException e) {
            throw new CurrentAccountProductNotFoundException(productId);
        }
    }

    @Override
    public CurrentAccountProductData retrieveTemplate() {
        this.context.authenticatedUser();

        final Collection<CurrencyData> currencyOptions = this.currencyReadPlatformService.retrieveAllowedCurrencies();

        return CurrentAccountProductData.template(currencyOptions);
    }

    /**
     * Row mapper for converting database rows to CurrentAccountProductData objects
     */
    private static final class CurrentAccountProductMapper implements RowMapper<CurrentAccountProductData> {

        public String schema() {
            return "cap.id, cap.name, cap.short_name, cap.description, "
                    + "cap.currency_code, cap.currency_digits, cap.currency_multiplesof, "
                    + "cap.minimum_opening_balance, cap.maintenance_fee_amount, cap.maintenance_fee_frequency, "
                    + "cap.overdraft_limit, cap.transaction_limit_per_day, cap.minimum_balance_for_interest_calculation, "
                    + "cap.interest_rate, cap.interest_calculation_type, cap.interest_posting_period_type, "
                    + "cap.is_active "
                    + "FROM m_current_account_product cap";
        }

        @Override
        public CurrentAccountProductData mapRow(final ResultSet rs, final int rowNum) throws SQLException {

            final Long id = rs.getLong("id");
            final String name = rs.getString("name");
            final String shortName = rs.getString("short_name");
            final String description = rs.getString("description");

            final String currencyCode = rs.getString("currency_code");
            final Integer currencyDigits = rs.getInt("currency_digits");
            final Integer inMultiplesOf = rs.getInt("currency_multiplesof");
            final CurrencyData currency = new CurrencyData(currencyCode, "", currencyDigits, inMultiplesOf, "", "");

            final BigDecimal minimumOpeningBalance = rs.getBigDecimal("minimum_opening_balance");
            final BigDecimal maintenanceFeeAmount = rs.getBigDecimal("maintenance_fee_amount");
            final Integer maintenanceFeeFrequency = rs.getInt("maintenance_fee_frequency");
            final BigDecimal overdraftLimit = rs.getBigDecimal("overdraft_limit");
            final BigDecimal transactionLimitPerDay = rs.getBigDecimal("transaction_limit_per_day");
            final BigDecimal minimumBalanceForInterestCalculation = rs.getBigDecimal("minimum_balance_for_interest_calculation");
            final BigDecimal interestRate = rs.getBigDecimal("interest_rate");
            final Integer interestCalculationType = rs.getInt("interest_calculation_type");
            final Integer interestPostingPeriodType = rs.getInt("interest_posting_period_type");
            final boolean active = rs.getBoolean("is_active");

            return new CurrentAccountProductData(id, name, shortName, description, currency,
                    minimumOpeningBalance, maintenanceFeeAmount, maintenanceFeeFrequency,
                    overdraftLimit, transactionLimitPerDay, minimumBalanceForInterestCalculation,
                    interestRate, interestCalculationType, interestPostingPeriodType, active);
        }
    }
}