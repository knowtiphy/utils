package org.knowtiphy.utils;

import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDFS;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author graham
 */
public class JenaUtils
{
	public static void printModel(StmtIterator it, String pre, Predicate<Statement> predicate)
	{
		while (it.hasNext())
		{
			var stmt = it.next();
			if (predicate.test(stmt))
			{
				System.err.print(pre + " ");
				System.err.println(stmt);
			}
		}
	}

	public static void printModel(Model model, String pre)
	{
		printModel(model.listStatements(), pre, x -> true);
	}

	public static void printModel(Model model, String pre, Predicate<Statement> predicate)
	{
		printModel(model.listStatements(), pre, predicate);
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
		} catch (NoSuchElementException ex)
		{
			return defaultValue;
		}
	}

	public static int getI(Model model, String subject, String predicate, int defaultValue)
	{
		try
		{
			return getI(listObjectsOfProperty(model, subject, predicate).next());
		} catch (NoSuchElementException ex)
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

	public static LocalDateTime getLDT(RDFNode node)
	{
		return ZonedDateTime.parse(node.asLiteral().getLexicalForm(),
				DateTimeFormatter.ISO_DATE_TIME).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static LocalDate getLD(Statement s)
	{
		return fromDate((XSDDateTime) s.getObject().asLiteral().getValue());
	}

	public static void addSubClasses(Model model, String subClass, String superClass)
	{
		model.add(model.createResource(subClass), RDFS.subClassOf, model.createResource(superClass));
	}

	public static Calendar fromDate(Date date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	public static LocalDate fromDate(XSDDateTime date)
	{
		return LocalDate.ofInstant(date.asCalendar().getTime().toInstant(), ZoneId.systemDefault());
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
}