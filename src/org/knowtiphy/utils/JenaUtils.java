package org.knowtiphy.utils;

import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import java.io.StringWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author graham
 * <p>
 * Various utility methods to help with model creation, querying etc
 */
public class JenaUtils
{
	//	methods to print a model and convert it to a string
	public static void printModel(Model model, String pre, StringWriter pw)
	{
		pw.append(pre).append("\n");
		RDFDataMgr.write(pw, model, RDFFormat.NTRIPLES);
	}

	public static void printModel(Model model, String pre)
	{
		var sw = new StringWriter();
		printModel(model, pre, sw);
		System.out.println(sw);
	}

	public static void printModel(Model model)
	{
		var sw = new StringWriter();
		printModel(model, "", sw);
		System.out.println(sw);
	}

	public static String toString(Model model)
	{
		var sw = new StringWriter();
		RDFDataMgr.write(sw, model, RDFFormat.NTRIPLES);
		return sw.toString();
	}

	//	helper methods for creating resources and literals

	public static Resource R(Model model, String uri)
	{
		return model.createResource(uri);
	}

	public static Property P(Model model, String uri)
	{
		return model.createProperty(uri);
	}

	public static <T> Literal L(Model model, T value)
	{
		assert !(value instanceof Literal);
		return model.createTypedLiteral(value);
	}

	//	get objects with a given subject and predicate

	public static NodeIterator listObjectsOfProperty(Model model, String s, String p)
	{
		return model.listObjectsOfProperty(R(model, s), P(model, p));
	}

	//	this is weird -- surely should be done via unique?
	public static String listSubjectsOfType(Model model, String type)
	{
		return model.listSubjectsWithProperty(RDF.type, R(model, type)).next().toString();
	}

	//	extract an (s, p, o) from a model, checking that there is exactly one match in the model
	public static <T> T unique(Model model, String s, String p, Function<RDFNode, T> f)
	{
		var stmts = listObjectsOfProperty(model, s, p);
		assert stmts.hasNext();
		T result = f.apply(stmts.next());
		assert !stmts.hasNext();
		return result;
	}

	//	extract an (s, p, o) from a model, checking that there is at most one match in the model
	public static <T> T atMostOne(Model model, String s, String p, Function<RDFNode, T> f)
	{
		var stmts = listObjectsOfProperty(model, s, p);
		if (!stmts.hasNext())
		{
			return null;
		}
		else
		{
			T result = f.apply(stmts.next());
			assert !stmts.hasNext();
			return result;
		}
	}

	//	extract a value from a one row result set
	public static <T> T unique(ResultSet resultSet, Function<QuerySolution, T> f)
	{
		assert resultSet.hasNext();
		var result = f.apply(resultSet.next());
		assert !resultSet.hasNext();
		return result;
	}

	//	get the o from (s, p, o) as a resource
	//	note: assumes that the object exists and is a resource, and there is only one of them
	public static Resource getOR(Model model, String s, String p)
	{
		return unique(model, s, p, RDFNode::asResource);
	}

	//	get the o from (s, p, o) as a literal
	//	note: assumes that the object exists and is a literal, and there is only one of them
	public static Literal getOL(Model model, String s, String p)
	{
		return unique(model, s, p, RDFNode::asLiteral);
	}

	//	get the o from (s, p, o) as a literal and convert it into some result type
	//	note: assumes that the object exists and is a literal, and there is only one of them
	public static <T> T getOL(Model model, String s, String p, Function<Literal, T> f)
	{
		return f.apply(getOL(model, s, p));
	}

	//	get the o from (s, p, o) as a literal and convert it into some result type
	//	if o isn't there then return a default value
	//	note: assumes that the object exists and is a literal, and there is only one of them

	public static <T> T getOL(Model model, String s, String p, Function<Literal, T> f, T defaultValue)
	{
		try
		{
			return getOL(model, s, p, f);
		}
		catch (NoSuchElementException ex)
		{
			return defaultValue;
		}
	}

	//	methods to extract booleans from various things

	public static boolean getB(RDFNode node)
	{
		return node.asLiteral().getBoolean();
	}

	public static Boolean getB(Model model, String s, String p)
	{
		return getOL(model, s, p, JenaUtils::getB);
	}

	//	methods to extract doubles from various things

	public static double getD(RDFNode node)
	{
		return node.asLiteral().getDouble();
	}

	public static double getD(Model model, String s, String p, double defaultValue)
	{
		return getOL(model, s, p, JenaUtils::getD, defaultValue);
	}

	//	methods to extract integers from various things

	public static int getI(RDFNode node)
	{
		return node.asLiteral().getInt();
	}

	//	methods to extract strings from various things

	public static String getS(RDFNode node)
	{
		return node.asLiteral().getString();
	}

	public static String getS(Model model, String s, String p)
	{
		return getOL(model, s, p, JenaUtils::getS);
	}

	public static String getS(QuerySolution soln, String var)
	{
		return getS(soln.get(var));
	}

	//	methods to extract byte arrays from various things

	public static byte[] getBA(QuerySolution soln, String var)
	{
		return (byte[]) soln.get(var).asLiteral().getValue();
	}

	//	methods to extract date-times from various things

	public static ZonedDateTime getDT(RDFNode node)
	{
		return ZonedDateTime.parse(node.asLiteral().getLexicalForm(),
				DateTimeFormatter.ISO_DATE_TIME).withZoneSameInstant(ZoneId.systemDefault());
	}

	public static ZonedDateTime getDT(Model model, String s, String p)
	{
		return getOL(model, s, p, JenaUtils::getDT);
	}

	public static ZonedDateTime getDT(QuerySolution soln, String var)
	{
		return getDT(soln.get(var));
	}

	//	apply a function over a list of objects

	public static void apply(Model model, String s, String p, Consumer<RDFNode> f)
	{
		JenaUtils.listObjectsOfProperty(model, s, p).forEachRemaining(f);
	}

	//	collect a collection of objects from various things

	public static <T> Collection<T> collect(Model model, String id, String prop,
											Function<RDFNode, T> f, Collection<T> result)
	{
		apply(model, id, prop, x -> result.add(f.apply(x)));
		return result;
	}

	public static <T> Collection<T> collect(Model model, String id, String prop, Function<RDFNode, T> f)
	{
		return collect(model, id, prop, f, new LinkedList<>());
	}

	public static <T> Collection<T> collect(ResultSet resultSet, Function<QuerySolution, T> f, Collection<T> result)
	{
		resultSet.forEachRemaining(soln -> result.add(f.apply(soln)));
		return result;
	}

	public static <T> Collection<T> collect(ResultSet resultSet, Function<QuerySolution, T> f)
	{
		return collect(resultSet, f, new LinkedList<>());
	}

	//	methods for adding statements to models

	public static void addType(Model model, String s, String type)
	{
		model.add(model.createStatement(R(model, s), RDF.type, R(model, type)));
	}

	public static void addOP(Model model, String s, String p, String o)
	{
		model.add(model.createStatement(R(model, s), P(model, p), R(model, o)));
	}

	public static <T> void addDP(Model model, String s, String p, T o)
	{
		assert o != null;
		model.add(model.createStatement(R(model, s), P(model, p), L(model, o)));
	}

	public static <T> void addDPN(Model model, String s, String p, T o)
	{
		//	ignore o if its null
		if (o != null)
		{
			addDP(model, s, p, o);
		}
	}

	//	methods to help make RDFS inferencing models

	public static Model addSubClasses(Model model, Map<String, String> subClasses)
	{
		subClasses.forEach((sub, sup) -> model.add(R(model, sub), RDFS.subClassOf, R(model, sup)));
		return model;
	}

	public static Model createRDFSModel(Model base, Map<String, String> subClasses)
	{
		return JenaUtils.addSubClasses(ModelFactory.createRDFSModel(base), subClasses);
	}

	//	here because its used in conjunction with fetching RDF data
	//	this seems kinda wierd to me ....
	public static XSDDateTime fromZDT(ZonedDateTime zdt)
	{
		return new XSDDateTime(GregorianCalendar.from(zdt));
	}
}

//	public static ZonedDateTime fromDate(XSDDateTime date)
//	{
//		return ZonedDateTime.ofInstant(date.asCalendar().getTime().toInstant(), ZoneId.systemDefault());
//	}