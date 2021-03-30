package org.knowtiphy.utils;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
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
		RDFDataMgr.write(sw, model, RDFFormat.TURTLE);
		return sw.toString();
	}

	public static boolean hasUnique(Model model, String s, String p)
	{
		var stmts = listObjectsOfProperty(model, s, p);
		if (!stmts.hasNext())
		{
			return false;
		}
		stmts.next();
		return !stmts.hasNext();
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

	public static String listSubjectsOfType(Model model, String type)
	{
		return model.listSubjectsWithProperty(RDF.type, R(model, type)).next().toString();
	}

	public static RDFNode listObjectsOfPropertyU(Model model, String s, String p)
	{
		return listObjectsOfProperty(model, s, p).next();
	}

	//	methods to extract objects from models
	//	These methods all assume the objects exist

	//	get the o from (s, p, o) as a resource (assumes that the object exists)
	public static Resource getOR(Model model, String s, String p)
	{
		return listObjectsOfProperty(model, s, p).next().asResource();
	}

	//	get the o from (s, p, o) as a literal (assumes that the object exists)
	public static Literal getOL(Model model, String s, String p)
	{
		return listObjectsOfProperty(model, s, p).next().asLiteral();
	}

	//	get the o from (s, p, o) as a literal and convert it into some result type
	public static <T> T getOL(Model model, String s, String p, Function<Literal, T> converter)
	{
		return converter.apply(getOL(model, s, p));
	}

	//	get the o from (s, p, o) as a literal and convert it into some result type
	//	if o isn't there then return a default value
	public static <T> T getOL(Model model, String s, String p,
							  Function<Literal, T> converter, T defaultValue)
	{
		try
		{
			return getOL(model, s, p, converter);
		}
		catch (NoSuchElementException ex)
		{
			return defaultValue;
		}
	}

	//	methods to extract literal values from statements (used in Peer updaters)
	//	These methods all assume the literal exists -- if not a NoSuchElementException is
	//	thrown by Jena

	public static boolean getB(Statement stmt)
	{
		return stmt.getObject().asLiteral().getBoolean();
	}

	public static int getI(Statement stmt)
	{

		return stmt.getObject().asLiteral().getInt();
	}

	public static String getS(Statement stmt)
	{
		return stmt.getObject().asLiteral().getString();
	}

	public static ZonedDateTime getDate(Statement s)
	{
		return ZonedDateTime.parse(s.getObject().asLiteral().getLexicalForm(),
				DateTimeFormatter.ISO_DATE_TIME).withZoneSameInstant(ZoneId.systemDefault());
	}

	//	methods to extract literal values from models, and if they don't exist, return
	//	default values

	public static Boolean getB(Model model, String s, String p)
	{
		return getOL(model, s, p, Literal::getBoolean);
	}

	public static double getOD(Model model, String s, String p, double defaultValue)
	{
		return getOL(model, s, p, Literal::getDouble, defaultValue);
	}

	public static String getS(Model model, String s, String p)
	{
		return getOL(model, s, p, Literal::getString);
	}

	//	methods to extract objects from query solutions

	public static ZonedDateTime getDate(QuerySolution soln, String var)
	{
		return ZonedDateTime.parse(soln.get(var).asLiteral().getLexicalForm(),
				DateTimeFormatter.ISO_DATE_TIME).withZoneSameInstant(ZoneId.systemDefault());
	}

	//	methods to extract objects from RDFNodes

	public static boolean getB(RDFNode node)
	{
		return node.asLiteral().getBoolean();
	}

	public static int getI(RDFNode node)
	{
		return node.asLiteral().getInt();
	}

	public static String getS(RDFNode node)
	{
		return node.asLiteral().getString();
	}


	public static ZonedDateTime getDate(RDFNode node)
	{
		return ZonedDateTime.parse(node.asLiteral().getLexicalForm(),
				DateTimeFormatter.ISO_DATE_TIME).withZoneSameInstant(ZoneId.systemDefault());
	}


	//	apply a function over a list of objects
	public static void apply(Model model, String s, String p, Consumer<RDFNode> f)
	{
		JenaUtils.listObjectsOfProperty(model, s, p).forEachRemaining(f);
	}

	//	collect a collection of objects from model triples
	public static <T> Collection<T> apply(Model model, String id, String prop,
										  Function<RDFNode, T> f, Collection<T> result)
	{
		apply(model, id, prop, x -> result.add(f.apply(x)));
		return result;
	}

	//	TODO --	all the code should work with result sets
	//	methods to extract values from result sets

	public static <T> Collection<T> collect(ResultSet resultSet, Collection<T> result, Function<QuerySolution, T> f)
	{
		resultSet.forEachRemaining(soln -> result.add(f.apply(soln)));
		return result;
	}

	public static <T> T single(ResultSet resultSet, Function<QuerySolution, T> f)
	{
		return f.apply(resultSet.next());
	}

	//	here because its used in conjunction with fetching RDF data
	public static GregorianCalendar fromDate(ZonedDateTime date)
	{
		return GregorianCalendar.from(date);
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
		subClasses.forEach((sub, sup) ->
				model.add(R(model, sub), RDFS.subClassOf, R(model, sup)));
		return model;
	}

	public static Model createRDFSModel(Model base, Map<String, String> subClasses)
	{
		return JenaUtils.addSubClasses(ModelFactory.createRDFSModel(base), subClasses);
	}
}


//	get subjects with a given predicate and object
//	public static ResIterator listSubjectsWithProperty(Model model, String predicate, String object)
//	{
//		return model.listSubjectsWithProperty(P(model, predicate), R(model, object));
//	}
//
//	public static ResIterator listTypes(Model model, String type)
//	{
//		return model.listSubjectsWithProperty(RDF.type, R(model, type));
//	}
//
//	public static ZonedDateTime fromDate(XSDDateTime date)
//	{
//		return ZonedDateTime.ofInstant(date.asCalendar().getTime().toInstant(), ZoneId.systemDefault());
//	}

//	public static byte[] getBytes(RDFNode node)
//	{
//		return (byte[]) node.asLiteral().getValue();
//	}