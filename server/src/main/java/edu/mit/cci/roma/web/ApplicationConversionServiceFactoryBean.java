package edu.mit.cci.roma.web;

import edu.mit.cci.roma.excel.ExcelSimulation;
import edu.mit.cci.roma.excel.ExcelVariable;
import edu.mit.cci.roma.impl.DefaultScenario;
import edu.mit.cci.roma.impl.DefaultSimulation;
import edu.mit.cci.roma.impl.DefaultVariable;
import edu.mit.cci.roma.impl.Tuple;
import edu.mit.cci.roma.server.CompositeScenario;
import edu.mit.cci.roma.server.CompositeServerSimulation;
import edu.mit.cci.roma.server.CompositeStepMapping;
import edu.mit.cci.roma.server.MappedServerSimulation;
import edu.mit.cci.roma.server.ScenarioList;
import edu.mit.cci.roma.server.Step;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.roo.addon.web.mvc.controller.RooConversionService;
        
/**
 * A central place to register application Converters and Formatters. 
 */
@RooConversionService
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean {

	@Override
	protected void installFormatters(FormatterRegistry registry) {
		super.installFormatters(registry);
		// Register application converters and formatters
	}

    Converter<ExcelSimulation, String> getExcelSimulationConverter() {
        return new Converter<ExcelSimulation, String>() {
            public String convert(ExcelSimulation source) {
                return new StringBuilder().append(source.getCreation()).toString();
            }
        };
    }

    Converter<ScenarioList, String> getScenarioListConverter() {
        return new Converter<ScenarioList, String>() {
            public String convert(ScenarioList source) {
                return new StringBuilder().append(source.toString()).toString();
            }
        };
    }

    Converter<DefaultVariable, String> getDefaultVariableConverter() {
        return new Converter<DefaultVariable, String>() {
            public String convert(DefaultVariable source) {
                return new StringBuilder().append(source.getName()).append(" ").append(source.getDescription()).append(" ").append(source.getArity()).toString();
            }
        };
    }

    Converter<CompositeScenario, String> getCompositeScenarioConverter() {
        return new Converter<CompositeScenario, String>() {
            public String convert(CompositeScenario source) {
                return new StringBuilder().append(source.getCreated()).append(" ").append(source.getLastStep()).toString();
            }
        };
    }

    Converter<CompositeStepMapping, String> getCompositeStepMappingConverter() {
        return new Converter<CompositeStepMapping, String>() {
            public String convert(CompositeStepMapping source) {
                return new StringBuilder().append(source.getFromStep()).append(" ").append(source.getToStep()).toString();
            }
        };
    }

    Converter<MappedServerSimulation, String> getMappedSimulationConverter() {
        return new Converter<MappedServerSimulation, String>() {
            public String convert(MappedServerSimulation source) {
                return new StringBuilder().append(source.getCreated()).append(" ").append(source.getSimulationVersion()).append(" ").append(source.getDescription()).toString();
            }
        };
    }

    Converter<DefaultSimulation, String> getDefaultSimulationConverter() {
        return new Converter<DefaultSimulation, String>() {
            public String convert(DefaultSimulation source) {
                return new StringBuilder().append(source.getCreated()).append(" ").append(source.getSimulationVersion()).append(" ").append(source.getDescription()).toString();
            }
        };
    }

    Converter<Step, String> getStepConverter() {
        return new Converter<Step, String>() {
            public String convert(Step source) {
                return new StringBuilder().append(source.getOrder_()).toString();
            }
        };
    }

    Converter<ExcelVariable, String> getExcelVariableConverter() {
        return new Converter<ExcelVariable, String>() {
            public String convert(ExcelVariable source) {
                return new StringBuilder().append(source.getWorksheetName()).append(" ").append(source.getCellRange()).toString();
            }
        };
    }

    Converter<CompositeServerSimulation, String>getCompositeSimulationConverter() {
        return new Converter<CompositeServerSimulation, String>() {
            public String convert(CompositeServerSimulation source) {
                return new StringBuilder().append(source.getCreated()).append(" ").append(source.getSimulationVersion()).append(" ").append(source.getDescription()).toString();
            }
        };
    }

    Converter<Tuple, String> getTupleConverter() {
        return new Converter<Tuple, String>() {
            public String convert(Tuple source) {
                return new StringBuilder().append(source.getValue_()).toString();
            }
        };
    }

    Converter<DefaultScenario, String> getDefaultScenarioConverter() {
        return new Converter<DefaultScenario, String>() {
            public String convert(DefaultScenario source) {
                return new StringBuilder().append(source.getCreated()).toString();
            }
        };
    }

    public void installLabelConverters(FormatterRegistry registry) {
        registry.addConverter(getExcelSimulationConverter());
        registry.addConverter(getScenarioListConverter());
        registry.addConverter(getDefaultVariableConverter());
        registry.addConverter(getCompositeScenarioConverter());
        registry.addConverter(getCompositeStepMappingConverter());
        registry.addConverter(getMappedSimulationConverter());
        registry.addConverter(getDefaultSimulationConverter());
        registry.addConverter(getStepConverter());
        registry.addConverter(getExcelVariableConverter());
        registry.addConverter(getCompositeSimulationConverter());
        registry.addConverter(getTupleConverter());
        registry.addConverter(getDefaultScenarioConverter());
    }

    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        installLabelConverters(getObject());
    }
	
}
