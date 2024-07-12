package iks.surveytool.mapping;

import iks.surveytool.entities.AbstractEntity;

import org.modelmapper.Converter;
import org.modelmapper.internal.util.MappingContextHelper;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.spi.MappingEngine;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.*;

@Component
public class ListConverter<T> implements Converter<List<?>, List<T>> {
    @Override
    public List<T> convert(MappingContext<List<?>, List<T>> context) {
        final MappingEngine mappingEngine = context.getMappingEngine();
        final List<?> source = context.getSource();
        if(source == null) return null;
        final List<T> dest = Objects.requireNonNullElseGet(context.getDestination(), ArrayList::new);
        Type elementType = MappingContextHelper.resolveDestinationGenericType(context);
        
        Iterator<?> sourceIt = source.iterator();
        ListIterator<T> destIt = dest.listIterator();
        
        while(sourceIt.hasNext() && destIt.hasNext()) {
            T destEl = destIt.next();
            Long oldId = null;
            if(destEl instanceof AbstractEntity) oldId = ((AbstractEntity) destEl).getId();
            T result;
            result = mappingEngine.map(context.create(sourceIt.next(), destEl));
            if(destEl instanceof AbstractEntity) ((AbstractEntity) destEl).setId(oldId);
            if(destEl != result) {
                if(result instanceof AbstractEntity) ((AbstractEntity) result).setId(null);
                destIt.set(destEl);
            }
        }
        
        while(destIt.hasNext()) {
            destIt.next();
            destIt.remove();
        }
        
        while(sourceIt.hasNext()) {
            T result = mappingEngine.map(context.create(sourceIt.next(), elementType));
            if(result instanceof AbstractEntity) ((AbstractEntity) result).setId(null);
            destIt.add(result);
        }
        
        return dest;
    }
}