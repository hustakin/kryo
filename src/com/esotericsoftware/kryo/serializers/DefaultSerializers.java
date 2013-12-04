
package com.esotericsoftware.kryo.serializers;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import static com.esotericsoftware.kryo.Kryo.*;
import static com.esotericsoftware.kryo.util.Util.*;
import static com.esotericsoftware.minlog.Log.TRACE;
import static com.esotericsoftware.minlog.Log.trace;

/** Contains many serializer classes that are provided by {@link Kryo#addDefaultSerializer(Class, Class) default}.
 * @author Nathan Sweet <misc@n4te.com> */
public class DefaultSerializers {
	static public class VoidSerializer extends Serializer {
		{
			setImmutable(true);
		}

		public void write (Kryo kryo, Output output, Object object) {
			
		}

		public Object read (Kryo kryo, Input input, Class type) {
			return null;
		}
	}
	static public class BooleanSerializer extends Serializer<Boolean> {
		{
			setImmutable(true);
		}

		public void write (Kryo kryo, Output output, Boolean object) {
			output.writeBoolean(object);
		}

		public Boolean read (Kryo kryo, Input input, Class<Boolean> type) {
			return input.readBoolean();
		}
	}

	static public class ByteSerializer extends Serializer<Byte> {
		{
			setImmutable(true);
		}

		public void write (Kryo kryo, Output output, Byte object) {
			output.writeByte(object);
		}

		public Byte read (Kryo kryo, Input input, Class<Byte> type) {
			return input.readByte();
		}
	}

	static public class CharSerializer extends Serializer<Character> {
		{
			setImmutable(true);
		}

		public void write (Kryo kryo, Output output, Character object) {
			output.writeChar(object);
		}

		public Character read (Kryo kryo, Input input, Class<Character> type) {
			return input.readChar();
		}
	}

	static public class ShortSerializer extends Serializer<Short> {
		{
			setImmutable(true);
		}

		public void write (Kryo kryo, Output output, Short object) {
			output.writeShort(object);
		}

		public Short read (Kryo kryo, Input input, Class<Short> type) {
			return input.readShort();
		}
	}

	static public class IntSerializer extends Serializer<Integer> {
		{
			setImmutable(true);
		}

		public void write (Kryo kryo, Output output, Integer object) {
			output.writeInt(object, false);
		}

		public Integer read (Kryo kryo, Input input, Class<Integer> type) {
			return input.readInt(false);
		}
	}

	static public class LongSerializer extends Serializer<Long> {
		{
			setImmutable(true);
		}

		public void write (Kryo kryo, Output output, Long object) {
			output.writeLong(object, false);
		}

		public Long read (Kryo kryo, Input input, Class<Long> type) {
			return input.readLong(false);
		}
	}

	static public class FloatSerializer extends Serializer<Float> {
		{
			setImmutable(true);
		}

		public void write (Kryo kryo, Output output, Float object) {
			output.writeFloat(object);
		}

		public Float read (Kryo kryo, Input input, Class<Float> type) {
			return input.readFloat();
		}
	}

	static public class DoubleSerializer extends Serializer<Double> {
		{
			setImmutable(true);
		}

		public void write (Kryo kryo, Output output, Double object) {
			output.writeDouble(object);
		}

		public Double read (Kryo kryo, Input input, Class<Double> type) {
			return input.readDouble();
		}
	}

	/** @see Output#writeString(String) */
	static public class StringSerializer extends Serializer<String> {
		{
			setImmutable(true);
			setAcceptsNull(true);
		}

		public void write (Kryo kryo, Output output, String object) {
			output.writeString(object);
		}

		public String read (Kryo kryo, Input input, Class<String> type) {
			return input.readString();
		}
	}

	static public class BigIntegerSerializer extends Serializer<BigInteger> {
		{
			setImmutable(true);
			setAcceptsNull(true);
		}

		public void write (Kryo kryo, Output output, BigInteger object) {
			if (object == null) {
				output.writeVarInt(NULL, true);
				return;
			}
			BigInteger value = (BigInteger)object;
			byte[] bytes = value.toByteArray();
			output.writeVarInt(bytes.length + 1, true);
			output.writeBytes(bytes);
		}

		public BigInteger read (Kryo kryo, Input input, Class<BigInteger> type) {
			int length = input.readVarInt(true);
			if (length == NULL) return null;
			byte[] bytes = input.readBytes(length - 1);
			return new BigInteger(bytes);
		}
	}

	static public class BigDecimalSerializer extends Serializer<BigDecimal> {
		private BigIntegerSerializer bigIntegerSerializer = new BigIntegerSerializer();

		{
			setAcceptsNull(true);
			setImmutable(true);
		}

		public void write (Kryo kryo, Output output, BigDecimal object) {
			if (object == null) {
				output.writeVarInt(NULL, true);
				return;
			}
			BigDecimal value = (BigDecimal)object;
			bigIntegerSerializer.write(kryo, output, value.unscaledValue());
			output.writeInt(value.scale(), false);
		}

		public BigDecimal read (Kryo kryo, Input input, Class<BigDecimal> type) {
			BigInteger unscaledValue = bigIntegerSerializer.read(kryo, input, null);
			if (unscaledValue == null) return null;
			int scale = input.readInt(false);
			return new BigDecimal(unscaledValue, scale);
		}
	}

	static public class ClassSerializer extends Serializer<Class> {
		{
			setImmutable(true);
			setAcceptsNull(true);
		}

		public void write (Kryo kryo, Output output, Class object) {
			kryo.writeClass(output, object);
			output.writeByte(object.isPrimitive()?1:0);
		}

		public Class read (Kryo kryo, Input input, Class<Class> type) {
			Registration registration = kryo.readClass(input);
			int isPrimitive = input.read();
			Class typ = registration.getType();
			if (!typ.isPrimitive()) return typ;
			return (isPrimitive == 1) ? typ : getWrapperClass(typ);
		}
	}

	static public class DateSerializer extends Serializer<Date> {
		public void write (Kryo kryo, Output output, Date object) {
			output.writeLong(object.getTime(), true);
		}

		public Date read (Kryo kryo, Input input, Class<Date> type) {
			return new Date(input.readLong(true));
		}

		public Date copy (Kryo kryo, Date original) {
			return new Date(original.getTime());
		}
	}

	static public class EnumSerializer extends Serializer<Enum> {
		{
			setImmutable(true);
			setAcceptsNull(true);
		}

		private Object[] enumConstants;

		public EnumSerializer (Class<? extends Enum> type) {
			enumConstants = type.getEnumConstants();
			if (enumConstants == null) throw new IllegalArgumentException("The type must be an enum: " + type);
		}

		public void write (Kryo kryo, Output output, Enum object) {
			if (object == null) {
				output.writeVarInt(NULL, true);
				return;
			}
			output.writeVarInt(object.ordinal() + 1, true);
		}

		public Enum read (Kryo kryo, Input input, Class<Enum> type) {
			int ordinal = input.readVarInt(true);
			if (ordinal == NULL) return null;
			ordinal--;
			if (ordinal < 0 || ordinal > enumConstants.length - 1)
				throw new KryoException("Invalid ordinal for enum \"" + type.getName() + "\": " + ordinal);
			Object constant = enumConstants[ordinal];
			return (Enum)constant;
		}
	}

	static public class EnumSetSerializer extends Serializer<EnumSet> {
		public void write (Kryo kryo, Output output, EnumSet object) {
			Serializer serializer;
			if (object.isEmpty()) {
				EnumSet tmp = EnumSet.complementOf(object);
				if (tmp.isEmpty()) throw new KryoException("An EnumSet must have a defined Enum to be serialized.");
				serializer = kryo.writeClass(output, tmp.iterator().next().getClass()).getSerializer();
			} else {
				serializer = kryo.writeClass(output, object.iterator().next().getClass()).getSerializer();
			}
			output.writeInt(object.size(), true);
			for (Object element : object)
				serializer.write(kryo, output, element);
		}

		public EnumSet read (Kryo kryo, Input input, Class<EnumSet> type) {
			Registration registration = kryo.readClass(input);
			EnumSet object = EnumSet.noneOf(registration.getType());
			Serializer serializer = registration.getSerializer();
			int length = input.readInt(true);
			for (int i = 0; i < length; i++)
				object.add(serializer.read(kryo, input, null));
			return object;
		}

		public EnumSet copy (Kryo kryo, EnumSet original) {
			return EnumSet.copyOf(original);
		}
	}

	/** @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a> */
	static public class CurrencySerializer extends Serializer<Currency> {
		{
			setImmutable(true);
			setAcceptsNull(true);
		}

		public void write (Kryo kryo, Output output, Currency object) {
			output.writeString(object == null ? null : object.getCurrencyCode());
		}

		public Currency read (Kryo kryo, Input input, Class<Currency> type) {
			String currencyCode = input.readString();
			if (currencyCode == null) return null;
			return Currency.getInstance(currencyCode);
		}
	}

	/** @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a> */
	static public class StringBufferSerializer extends Serializer<StringBuffer> {
		{
			setAcceptsNull(true);
		}

		public void write (Kryo kryo, Output output, StringBuffer object) {
			output.writeString(object);
		}

		public StringBuffer read (Kryo kryo, Input input, Class<StringBuffer> type) {
			String value = input.readString();
			if (value == null) return null;
			return new StringBuffer(value);
		}

		public StringBuffer copy (Kryo kryo, StringBuffer original) {
			return new StringBuffer(original);
		}
	}

	/** @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a> */
	static public class StringBuilderSerializer extends Serializer<StringBuilder> {
		{
			setAcceptsNull(true);
		}

		public void write (Kryo kryo, Output output, StringBuilder object) {
			output.writeString(object);
		}

		public StringBuilder read (Kryo kryo, Input input, Class<StringBuilder> type) {
			return input.readStringBuilder();
		}

		public StringBuilder copy (Kryo kryo, StringBuilder original) {
			return new StringBuilder(original);
		}
	}

	static public class KryoSerializableSerializer extends Serializer<KryoSerializable> {
		public void write (Kryo kryo, Output output, KryoSerializable object) {
			object.write(kryo, output);
		}

		public KryoSerializable read (Kryo kryo, Input input, Class<KryoSerializable> type) {
			KryoSerializable object = kryo.newInstance(type);
			kryo.reference(object);
			object.read(kryo, input);
			return object;
		}
	}

	/** Serializer for lists created via {@link Collections#emptyList()} or that were just assigned the
	 * {@link Collections#EMPTY_LIST}.
	 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a> */
	static public class CollectionsEmptyListSerializer extends Serializer {
		{
			setImmutable(true);
		}

		public void write (Kryo kryo, Output output, Object object) {
		}

		public Object read (Kryo kryo, Input input, Class type) {
			return Collections.EMPTY_LIST;
		}
	}

	/** Serializer for maps created via {@link Collections#emptyMap()} or that were just assigned the {@link Collections#EMPTY_MAP}.
	 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a> */
	static public class CollectionsEmptyMapSerializer extends Serializer {
		{
			setImmutable(true);
		}

		public void write (Kryo kryo, Output output, Object object) {
		}

		public Object read (Kryo kryo, Input input, Class type) {
			return Collections.EMPTY_MAP;
		}
	}

	/** Serializer for sets created via {@link Collections#emptySet()} or that were just assigned the {@link Collections#EMPTY_SET}.
	 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a> */
	static public class CollectionsEmptySetSerializer extends Serializer {
		{
			setImmutable(true);
		}

		public void write (Kryo kryo, Output output, Object object) {
		}

		public Object read (Kryo kryo, Input input, Class type) {
			return Collections.EMPTY_SET;
		}
	}

	/** Serializer for lists created via {@link Collections#singletonList(Object)}.
	 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a> */
	static public class CollectionsSingletonListSerializer extends Serializer<List> {
		{
			setImmutable(true);
		}

		public void write (Kryo kryo, Output output, List object) {
			kryo.writeClassAndObject(output, object.get(0));
		}

		public List read (Kryo kryo, Input input, Class type) {
			return Collections.singletonList(kryo.readClassAndObject(input));
		}
	}

	/** Serializer for maps created via {@link Collections#singletonMap(Object, Object)}.
	 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a> */
	static public class CollectionsSingletonMapSerializer extends Serializer<Map> {
		{
			setImmutable(true);
		}

		public void write (Kryo kryo, Output output, Map object) {
			Entry entry = (Entry)object.entrySet().iterator().next();
			kryo.writeClassAndObject(output, entry.getKey());
			kryo.writeClassAndObject(output, entry.getValue());
		}

		public Map read (Kryo kryo, Input input, Class type) {
			Object key = kryo.readClassAndObject(input);
			Object value = kryo.readClassAndObject(input);
			return Collections.singletonMap(key, value);
		}
	}

	/** Serializer for sets created via {@link Collections#singleton(Object)}.
	 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a> */
	static public class CollectionsSingletonSetSerializer extends Serializer<Set> {
		{
			setImmutable(true);
		}

		public void write (Kryo kryo, Output output, Set object) {
			kryo.writeClassAndObject(output, object.iterator().next());
		}

		public Set read (Kryo kryo, Input input, Class type) {
			return Collections.singleton(kryo.readClassAndObject(input));
		}
	}

	/** Serializer for {@link TimeZone}. Assumes the timezones are immutable.
	 * @author serverperformance */
	static public class TimeZoneSerializer extends Serializer<TimeZone> {
		{
			setImmutable(true);
		}

		public void write (Kryo kryo, Output output, TimeZone object) {
			output.writeString(object.getID());
		}

		public TimeZone read (Kryo kryo, Input input, Class<TimeZone> type) {
			return TimeZone.getTimeZone(input.readString());
		}
	}

	/** Serializer for {@link GregorianCalendar}, java.util.JapaneseImperialCalendar, and sun.util.BuddhistCalendar.
	 * @author serverperformance */
	static public class CalendarSerializer extends Serializer<Calendar> {
		// The default value of gregorianCutover.
		static private final long DEFAULT_GREGORIAN_CUTOVER = -12219292800000L;

		TimeZoneSerializer timeZoneSerializer = new TimeZoneSerializer();

		public void write (Kryo kryo, Output output, Calendar object) {
			timeZoneSerializer.write(kryo, output, object.getTimeZone()); // can't be null
			output.writeLong(object.getTimeInMillis(), true);
			output.writeBoolean(object.isLenient());
			output.writeInt(object.getFirstDayOfWeek(), true);
			output.writeInt(object.getMinimalDaysInFirstWeek(), true);
			if (object instanceof GregorianCalendar)
				output.writeLong(((GregorianCalendar)object).getGregorianChange().getTime(), false);
			else
				output.writeLong(DEFAULT_GREGORIAN_CUTOVER, false);
		}

		public Calendar read (Kryo kryo, Input input, Class<Calendar> type) {
			Calendar result = Calendar.getInstance(timeZoneSerializer.read(kryo, input, TimeZone.class));
			result.setTimeInMillis(input.readLong(true));
			result.setLenient(input.readBoolean());
			result.setFirstDayOfWeek(input.readInt(true));
			result.setMinimalDaysInFirstWeek(input.readInt(true));
			long gregorianChange = input.readLong(false);
			if (gregorianChange != DEFAULT_GREGORIAN_CUTOVER)
				if (result instanceof GregorianCalendar) ((GregorianCalendar)result).setGregorianChange(new Date(gregorianChange));
			return result;
		}

		public Calendar copy (Kryo kryo, Calendar original) {
			return (Calendar)original.clone();
		}
	}

	static public class TreeMapSerializer extends MapSerializer {
		public void write (Kryo kryo, Output output, Map map) {
			TreeMap treeMap = (TreeMap)map;
			kryo.writeClassAndObject(output, treeMap.comparator());
			super.write(kryo, output, map);
		}

		protected Map create (Kryo kryo, Input input, Class<Map> type) {
			return new TreeMap((Comparator)kryo.readClassAndObject(input));
		}

		protected Map createCopy (Kryo kryo, Map original) {
			return new TreeMap(((TreeMap)original).comparator());
		}
	}

	static public class TreeSetSerializer extends CollectionSerializer {
		public void write (Kryo kryo, Output output, Collection collection) {
			TreeSet treeSet = (TreeSet)collection;
			kryo.writeClassAndObject(output, treeSet.comparator());
			super.write(kryo, output, collection);
		}

		protected TreeSet create (Kryo kryo, Input input, Class<Collection> type) {
			return new TreeSet((Comparator)kryo.readClassAndObject(input));
		}

		protected TreeSet createCopy (Kryo kryo, Collection original) {
			return new TreeSet(((TreeSet)original).comparator());
		}
	}

	/**
	 * Serializer for {@link Locale}.
	 * <p>
	 * For deserialization by default (and if available) the static method <code>Locale.getInstance(String, String, String)</code>
	 * is used via reflection to make use of the Locale cache.<br/>
	 * To use the {@link Locale#Locale(String, String, String) Locale(String, String, String)} constructor instead the
	 * {@link LocaleSerializer#LocaleSerializer(boolean) LocaleSerializer(boolean)} has to be used with <code>useReflection</code>
	 * set to <code>false</code>.
	 * </p>
	 *
	 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
	 */
	static public class LocaleSerializer extends Serializer<Locale> {

		private static Method getInstance;

		static {
			try {
				getInstance = Locale.class.getDeclaredMethod("getInstance", String.class, String.class, String.class);
				getInstance.setAccessible(true);
			} catch (final Exception e) {
				if (TRACE) trace("kryo", "java.util.Locale.getInstance is not available");
				getInstance = null;
			}
		}

		private final boolean useReflection;

		/**
		 * Creates a new instance with {@link #useReflection} set to <code>true</code>.
		 *
		 * @see #LocaleSerializer(boolean)
		 */
		public LocaleSerializer() {
			this(true);
		}

		/**
		 * Creates a new instance and activates the usage of reflection (for getting the
		 * {@link Locale} during deserialization) if <code>true</code> was provided for <code>useReflection</code>
		 * and if the static method <code>Locale.getInstance(String, String, String)</code> is available.
		 *
		 * @param useReflection asks to activate reflection usage.
		 */
		public LocaleSerializer(final boolean useReflection) {
			setImmutable(true);
			this.useReflection = getInstance != null && useReflection;
		}

		@Override
		public void write (Kryo kryo, Output output, Locale object) {
			output.writeString(object.getLanguage());
			output.writeString(object.getCountry());
			output.writeString(object.getVariant());
		}

		@Override
		public Locale read (Kryo kryo, Input input, Class<Locale> type) {
			final String language = input.readString();
			final String country = input.readString();
			final String variant = input.readString();
			if(useReflection) {
				try {
					return (Locale) getInstance.invoke(null, language, country, variant);
				} catch (final Exception e) {
					throw new RuntimeException("Could not get Locale using Locale.getInstance", e);
				}
			}
			return new Locale(language, country, variant);
		}
	}
}
