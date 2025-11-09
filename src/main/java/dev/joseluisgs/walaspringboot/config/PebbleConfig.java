package dev.joseluisgs.walaspringboot.config;

import io.pebbletemplates.boot.autoconfigure.PebbleAutoConfiguration;
import io.pebbletemplates.pebble.extension.AbstractExtension;
import io.pebbletemplates.pebble.extension.Filter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Configuration
@AutoConfigureBefore(PebbleAutoConfiguration.class)
public class PebbleConfig {

    @Bean
    public io.pebbletemplates.pebble.extension.Extension customPebbleExtension() {
        return new AbstractExtension() {
            @Override
            public Map<String, Filter> getFilters() {
                Map<String, Filter> filters = new HashMap<>();
                filters.put("formatDate", new FormatDateFilter());
                filters.put("formatPrice", new FormatPriceFilter());
                filters.put("formatMonth", new FormatMonthFilter());
                filters.put("formatDateTime", new FormatDateTimeFilter());
                return filters;
            }
        };
    }

    // Filtro para formatear fechas
    private static class FormatDateFilter implements Filter {
        @Override
        public Object apply(Object input, Map<String, Object> args, io.pebbletemplates.pebble.template.PebbleTemplate self,
                            io.pebbletemplates.pebble.template.EvaluationContext context, int lineNumber) throws io.pebbletemplates.pebble.error.PebbleException {
            if (input == null) {
                return "";
            }

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", new Locale("es", "ES"));

                if (input instanceof LocalDateTime) {
                    return ((LocalDateTime) input).format(formatter);
                } else if (input instanceof Date) {
                    SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy, HH:mm", new Locale("es", "ES"));
                    return sdf.format((Date) input);
                } else if (input instanceof String) {
                    // Try to parse as LocalDateTime
                    try {
                        LocalDateTime dateTime = LocalDateTime.parse(input.toString());
                        return dateTime.format(formatter);
                    } catch (Exception e) {
                        return input.toString();
                    }
                }
            } catch (Exception e) {
                return input.toString();
            }

            return input.toString();
        }

        @Override
        public List<String> getArgumentNames() {
            return null;
        }
    }

    // Filtro para formatear mes de una fecha
    private static class FormatMonthFilter implements Filter {
        @Override
        public Object apply(Object input, Map<String, Object> args, io.pebbletemplates.pebble.template.PebbleTemplate self,
                            io.pebbletemplates.pebble.template.EvaluationContext context, int lineNumber) throws io.pebbletemplates.pebble.error.PebbleException {
            if (input == null) {
                return "";
            }

            try {
                if (input instanceof LocalDateTime dateTime) {
                    return dateTime.getMonth().getDisplayName(
                            java.time.format.TextStyle.FULL,
                            new Locale("es", "ES")
                    );
                }
            } catch (Exception e) {
                return "";
            }

            return "";
        }

        @Override
        public List<String> getArgumentNames() {
            return null;
        }
    }

    // Filtro para formatear fecha y hora completa
    private static class FormatDateTimeFilter implements Filter {
        @Override
        public Object apply(Object input, Map<String, Object> args, io.pebbletemplates.pebble.template.PebbleTemplate self,
                            io.pebbletemplates.pebble.template.EvaluationContext context, int lineNumber) throws io.pebbletemplates.pebble.error.PebbleException {
            if (input == null) {
                return "";
            }

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", new Locale("es", "ES"));

                if (input instanceof LocalDateTime) {
                    return ((LocalDateTime) input).format(formatter);
                }
            } catch (Exception e) {
                return input.toString();
            }

            return input.toString();
        }

        @Override
        public List<String> getArgumentNames() {
            return null;
        }
    }

    // Filtro para formatear precios
    private static class FormatPriceFilter implements Filter {
        @Override
        public Object apply(Object input, Map<String, Object> args, io.pebbletemplates.pebble.template.PebbleTemplate self,
                            io.pebbletemplates.pebble.template.EvaluationContext context, int lineNumber) throws io.pebbletemplates.pebble.error.PebbleException {
            if (input == null) {
                return "0,00 €";
            }

            try {
                double price;
                if (input instanceof Number) {
                    price = ((Number) input).doubleValue();
                } else {
                    price = Double.parseDouble(input.toString());
                }

                DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("es", "ES"));
                symbols.setDecimalSeparator(',');
                symbols.setGroupingSeparator('.');

                DecimalFormat df = new DecimalFormat("#,##0.00 €", symbols);
                return df.format(price);
            } catch (Exception e) {
                return input + " €";
            }
        }

        @Override
        public List<String> getArgumentNames() {
            return null;
        }
    }
}
