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
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import java.io.PrintWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * @author graham
 */
public class JenaUtils
{
	private static final Logger LOGGER = Logger.getLogger(JenaUtils.class.getName());
	//public final static String RDF_TYPE = RDF.type.toString();

	public static void printModel(StmtIterator it, String pre, PrintWriter pw)
	{
		while (it.hasNext())
		{
			var stmt = it.next();
			pw.print(pre + " ");
			pw.println(stmt);
		}
	}

	public static void printModel(StmtIterator it, String pre, Predicate<Statement> predicate)
	{
		while (it.hasNext())
		{
			var stmt = it.next();
			if (predicate.test(stmt))
			{
				System.out.print(pre + " ");
				System.out.println(stmt);
			}
		}
	}

	public static void printModel(Model model, String pre)
	{
		printModel(model.listStatements(), pre, x -> true);
	}

//	public static void printModel(Model model, String pre, Predicate<Statement> predicate)
//	{
//		printModel(model.listStatements(), pre, predicate);
//	}

	public static String toString(String pre, Model model, Predicate<Statement> predicate)
	{
		StringBuilder builder = new StringBuilder();
		StmtIterator it = model.listStatements();
		if (it.hasNext())
		{
			builder.append("\n");
		}
		while (it.hasNext())
		{
			var stmt = it.next();
			if (predicate.test(stmt))
			{
				builder.append(pre).append(" ").append(stmt).append("\n");
			}
		}

		return builder.toString();
	}

	public static boolean checkUnique(Iterator stmts)
	{
		if (!stmts.hasNext())
		{
			return false;
		}
		stmts.next();
		return !stmts.hasNext();
	}

	public static NodeIterator listObjectsOfProperty(Model model, String subject, String predicate)
	{
		return model.listObjectsOfProperty(model.createResource(subject), model.createProperty(predicate));
	}

	public static RDFNode listObjectsOfPropertyU(Model model, String subject, String predicate)
	{
		return listObjectsOfProperty(model, subject, predicate).next();
	}

	public static ResIterator listSubjectsWithProperty(Model model, String predicate, String object)
	{
		return model.listSubjectsWithProperty(model.createProperty(predicate), model.createResource(object));
	}

	public static Resource listSubjectsWithPropertyU(Model model, String predicate, String object)
	{
		return listSubjectsWithProperty(model, predicate, object).next();
	}

	public static Resource listTypes(Model model, String type)
	{
		return listSubjectsWithProperty(model, RDF.type.toString(), type).next();
	}

	public static boolean getB(RDFNode node)
	{
		return node.asLiteral().getBoolean();
	}

	public static boolean getB(Statement stmt)
	{
		return getB(stmt.getObject());
	}

	public static double getD(RDFNode node)
	{
		return node.asLiteral().getDouble();
	}

	public static double getD(Model model, String subject, String predicate, double defaultValue)
	{
		try
		{
			return getD(listObjectsOfProperty(model, subject, predicate).next());
		}
		catch (NoSuchElementException ex)
		{
			return defaultValue;
		}
	}

	public static Resource getR(Model model, String subject, String predicate)
	{
		return listObjectsOfProperty(model, subject, predicate).next().asResource();
	}

	public static boolean getB(Model model, String subject, String predicate)
	{
		return getB(listObjectsOfProperty(model, subject, predicate).next());
	}

	public static int getI(Model model, String subject, String predicate, int defaultValue)
	{
		try
		{
			return getI(listObjectsOfProperty(model, subject, predicate).next());
		}
		catch (NoSuchElementException ex)
		{
			return defaultValue;
		}
	}

	public static boolean has(Model model, String subject, String predicate)
	{
		return listObjectsOfProperty(model, subject, predicate).hasNext();
	}

	public static int getI(RDFNode node)
	{
		return node.asLiteral().getInt();
	}

	public static int getI(Statement stmt)
	{
		return getI(stmt.getObject());
	}

	public static String getS(RDFNode node)
	{
		return node.asLiteral().getString();
	}

	public static String getS(Statement stmt)
	{
		return getS(stmt.getObject());
	}

	public static byte[] getBytes(RDFNode node)
	{
		return (byte[]) node.asLiteral().getValue();
	}

	public static ZonedDateTime getLDT(RDFNode node)
	{
		return ZonedDateTime.parse(node.asLiteral().getLexicalForm(),
				DateTimeFormatter.ISO_DATE_TIME).withZoneSameInstant(ZoneId.systemDefault());
	}

	public static ZonedDateTime getLD(Statement s)
	{
		return getLDT(s.getObject());
	}

	public static void addSubClass(Model model, String subClass, String superClass)
	{
		model.add(R(model, subClass), RDFS.subClassOf, R(model, superClass));
	}

	public static GregorianCalendar fromDate(ZonedDateTime date)
	{
		return GregorianCalendar.from(date);
	}

	public static ZonedDateTime fromDate(XSDDateTime date)
	{
		return ZonedDateTime.ofInstant(date.asCalendar().getTime().toInstant(), ZoneId.systemDefault());
	}

	public static <T> T single(ResultSet resultSet, Function<QuerySolution, T> f)
	{
		return f.apply(resultSet.next());
	}

	public static <T> Set<T> set(ResultSet resultSet, Function<QuerySolution, T> f)
	{
		Set<T> result = new HashSet<>();
		while (resultSet.hasNext())
		{
			result.add(f.apply(resultSet.next()));
		}

		return result;
	}

	//	helper methods

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
		if (o != null)
		{
			addDP(model, s, p, o);
		}
	}

	public static Model createRDFSModel(Model base, Map<String, String> subClasses)
	{
		var model = ModelFactory.createRDFSModel(base);
		JenaUtils.addSubClasses(model, subClasses);
		return model;
	}

	public static Model createRDFSModel(Map<String, String> subClasses)
	{
		return createRDFSModel(ModelFactory.createDefaultModel(), subClasses);
	}
	public static void addSubClasses(Model model, Map<String, String> subClasses)
	{
		subClasses.forEach((sub, sup) -> addSubClass(model, sub, sup));
	}

	public static void collect(Model model, String id, String prop, Consumer<RDFNode> f)
	{
		JenaUtils.listObjectsOfProperty(model, id, prop).forEachRemaining(f);
	}

	public static <T> Collection<T> collect(Collection<T> result, Model model,
										 String id, String prop, Function<RDFNode, T> f)
	{
		JenaUtils.listObjectsOfProperty(model, id, prop).forEachRemaining(x -> result.add(f.apply(x)));
		return result;
	}
}